package com.maaksoft.smartshop.activity;

import java.lang.reflect.Field;

import java.util.List;

import android.Manifest;

import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;

import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.Toast;

import android.media.RingtoneManager;

import android.net.wifi.WifiManager;

import android.telephony.TelephonyManager;

import android.database.Cursor;

import com.firebase.client.Firebase;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.ButterKnife;
import butterknife.BindView;

import com.maaksoft.smartshop.fragments.ContactListsFragment;
import com.maaksoft.smartshop.fragments.SettingsFragment;
import com.maaksoft.smartshop.fragments.StoreListsFragment;
import com.maaksoft.smartshop.fragments.ShopListsFragment;

import com.maaksoft.smartshop.messaging.FirebaseMessagingService;
import com.maaksoft.smartshop.model.Contact;

import com.maaksoft.smartshop.network.DataReceiver;
import com.maaksoft.smartshop.network.DataSender;
import com.maaksoft.smartshop.network.SmsListener;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.utils.RESTCommunication;

import com.maaksoft.smartshop.BuildConfig;
import com.maaksoft.smartshop.R;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";

    private static final int PERMISSION_REQUEST_ACCESS_PHONE = 100;
    private static final int PERMISSION_REQUEST_ACCESS_CONTACTS = 200;

    @BindView(R.id.adView)
    AdView adView;

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    private String firebaseTokenId;
    private String ownerName;
    private String phoneNumber;

    private void startServer() {

        if (DataReceiver.ipAddress == null) {

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            int ipAddressInt = wifiManager.getConnectionInfo().getIpAddress();
            DataReceiver.ipAddress = String.format("%d.%d.%d.%d", (ipAddressInt & 0xff), (ipAddressInt >> 8 & 0xff), (ipAddressInt >> 16 & 0xff),
                    (ipAddressInt >> 24 & 0xff));
            System.out.println("IP Address: " + DataReceiver.ipAddress);

            RESTCommunication.setStrictMode();
            Intent serviceIntent = new Intent(this, DataReceiver.class);
            serviceIntent.putExtra(DataReceiver.IP_ADDRESS, DataReceiver.ipAddress);
            startService(serviceIntent);

        }

    }

    private void loadOwnerName() {

        if (this.ownerName == null) {

            try {

                Cursor cursor = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
                if(cursor.getCount() > 0) {

                    cursor.moveToFirst();
                    this.ownerName = cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
                    System.out.println("ownerName: " + this.ownerName);

                }
                cursor.close();

            } catch (Throwable t) {
                t.printStackTrace();
                requestContactsPermission();
            }

        }

    }

    private boolean requestContactsPermission() {

        String permission = Manifest.permission.READ_CONTACTS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, PERMISSION_REQUEST_ACCESS_CONTACTS);
            return false;
        } else {
            return true;
        }

    }

    private boolean requestReadPhonePermission() {

        String permission = Manifest.permission.READ_PHONE_STATE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                                              new String[] { permission, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                                              PERMISSION_REQUEST_ACCESS_PHONE);
            return false;
        } else {
            return true;
        }

    }

    private void requestSmsPermission() {

        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { permission, Manifest.permission.SEND_SMS }, 1);
        }

    }

    private void registerSmsReceiver() {

        BroadcastReceiver smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String ipAddress = intent.getStringExtra(SmsListener.SMS_MESSAGE_SENDER_IP_KEY);
                String message = ("Message received from IP: " + ipAddress);
                System.out.println(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.notification_icon).
                        setContentTitle("Smart Shop related SMS").setContentText(message).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                        setContentIntent(contentIntent);
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

                DataSender.globalSenderIAddress = ipAddress;

            }

        };

        super.registerReceiver(smsReceiver, new IntentFilter(SmsListener.SMS_MESSAGE_RECEIVED_FILTER_NAME));

    }

    private void disableShiftMode() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        try {

            Field shiftingMode = bottomNavigationMenuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(bottomNavigationMenuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {

                BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(i);
                bottomNavigationItemView.setShiftingMode(false);
                bottomNavigationItemView.setChecked(bottomNavigationItemView.getItemData().isChecked());

            }

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

    }

    private void loadAd() {

        try {

            MobileAds.initialize(this, BuildConfig.SMART_SHOP_AD_MOB_APP_ID);

            this.adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
            this.adView.setAdListener(new AdListener() {

                private void showToast(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

                private void showAd() {
                    //adView.setVisibility(View.VISIBLE);
                    frameLayout.setTop(50);
                }

                private void hideAd() {
                    adView.setVisibility(View.GONE);
                    frameLayout.setTop(5);
                }

                @Override
                public void onAdLoaded() {
                    showAd();
                    //showToast("Ad loaded.");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    hideAd();
                    showToast(String.format("Ad failed to load with error code %d.", errorCode));
                }

                @Override
                public void onAdOpened() {
                    //showToast("Ad opened.");
                }

                @Override
                public void onAdClosed() {
                    //showToast("Ad closed.");
                }

                @Override
                public void onAdLeftApplication() {
                    //showToast("Ad left application.");
                }

            });

        } catch (Throwable throwable) {

            throwable.printStackTrace();
            System.out.println(throwable.getClass() + " has occurred.");
            this.adView.setVisibility(View.GONE);

        }

    }

    private void loadPhoneNumber() {

        if (this.phoneNumber == null) {

            try {

                Contact contact = null;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SmartShopService smartShopService = new SmartShopService();
                List<Contact> contacts = smartShopService.getContactByName(this, Contact.SELF);
                if (contacts == null || contacts.isEmpty()) {

                    this.phoneNumber = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
                    this.firebaseTokenId = FirebaseInstanceId.getInstance().getToken();

                    System.out.println("Phone Number: " + this.phoneNumber);
                    System.out.println("Firebase Token ID: " + this.firebaseTokenId);

                    contact = new Contact(Contact.SELF, this.phoneNumber, this.firebaseTokenId);
                    smartShopService.addContact(this, contact);

                    contact = smartShopService.getContactByName(this, Contact.SELF).get(0);
                    contact.setName(("Contact for Phone " + this.phoneNumber));

                    Firebase.setAndroidContext(this);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(FirebaseMessagingService.CONTACTS_FIREBASE_URL);
                    databaseReference.push().setValue(contact);

                } else {

                    contact = contacts.get(0);
                    this.phoneNumber = contact.getPhoneNumber();
                    this.firebaseTokenId = contact.getKey();

                }

                System.out.println("firebaseTokenId: " + this.firebaseTokenId);
                preferences.edit().putString(FirebaseMessagingService.PHONE_NUMBER, this.phoneNumber).apply();
                preferences.edit().putString(FirebaseMessagingService.FIREBASE_TOKEN, this.firebaseTokenId).apply();

            } catch (Throwable t) {
                t.printStackTrace();
                requestReadPhonePermission();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        System.out.println("onRequestPermissionsResult called with requestCode: " + requestCode);
        if (requestCode == PERMISSION_REQUEST_ACCESS_PHONE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.loadPhoneNumber();
        } else if (requestCode == PERMISSION_REQUEST_ACCESS_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.loadOwnerName();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.loadPhoneNumber();
        //this.loadOwnerName();

        //String foundKey = FirebaseMessagingService.shareShopList(this, "1203856");
        //System.out.println("FOUND Key: " + foundKey);

        /*
        Contact contact = new Contact("Muhammad Ali Amir", this.phoneNumber, this.firebaseTokenId);
        contact.setShopListContactId(1);

        Firebase.setAndroidContext(this);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(FirebaseMessagingService.CONTACTS_FIREBASE_URL);
        databaseReference.push().setValue(contact);
        contact.setName("Asma Ahmed");
        contact.setPhoneNumber("12038562917");
        contact.setShopListContactId(2);
        databaseReference.push().setValue(contact);
        */

        ButterKnife.bind(this);

        /*
        this.requestSmsPermission();

        super.registerReceiver(new SmsListener(), new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        this.registerSmsReceiver();

        this.startServer();
        */

        //this.loadAd();

        final StoreListsFragment storesFragment = StoreListsFragment.newInstance();
        final ShopListsFragment shopListsFragment = ShopListsFragment.newInstance();
        Fragment fragment = shopListsFragment;

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {

                    /*
                    case R.id.add_shop_list_action:
                        selectedFragment = ShopListDetailFragment.newInstance();
                        break;
                    */

                    case R.id.shop_lists_action:
                        selectedFragment = shopListsFragment;
                        break;

                    case R.id.stores_actions:
                        selectedFragment = storesFragment;
                        break;

                    case R.id.settings_action:
                        selectedFragment = SettingsFragment.newInstance();
                        break;

                    case R.id.contacts_actions:
                        selectedFragment = ContactListsFragment.newInstance();
                        break;

                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;

            }

        });

        Intent intent = getIntent();
        Integer fragmentIndex = 2;
        if (intent != null) {

            fragmentIndex = intent.getIntExtra(FRAGMENT_INDEX, fragmentIndex);
            if (fragmentIndex == 0) {
                fragment = storesFragment;
            }

        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();

        bottomNavigationView.getMenu().getItem(fragmentIndex).setChecked(true);
        this.disableShiftMode();

    }

}