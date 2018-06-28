package com.maaksoft.smartshop.messaging;

import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Build;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.support.annotation.RequiresApi;

import android.support.v4.app.NotificationCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.media.RingtoneManager;

import android.preference.PreferenceManager;

import android.net.Uri;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.maaksoft.smartshop.BuildConfig;

import com.maaksoft.smartshop.model.Contact;
import com.maaksoft.smartshop.model.ShopList;

import com.maaksoft.smartshop.model.ShopListContact;
import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.utils.RESTCommunication;

import com.maaksoft.smartshop.R;

import com.maaksoft.smartshop.activity.MainActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static final String CONTACTS_FIREBASE_URL = (BuildConfig.FIREBASE_REALTIME_DATABASE_URL + "/contacts");
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String KEY = "key";
    public static final String NAME = "name";

    private static final String SUCCESS = "success";

    private static final String TO = "to";
    private static final String TITLE = "title";
    private static final String DATA = "data";
    private static final String MESSAGE = "message";
    private static final String SENDER_PHONE_NUMBER = "senderPhoneNumber";
    private static final String SENDER_KEY = "senderKey";
    private static final String SHOP_LIST_CONTENT = "shopListContent";

    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    private NotificationManager notificationManager;

    static class ShopListSharingListener implements ValueEventListener {

        private Context context;
        private ShopList shopList;

        public ShopListSharingListener(Context context, ShopList shopList) {
            this.context = context;
            this.shopList = shopList;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            DataSnapshot contactDataSnapshot = null;
            Iterator<DataSnapshot> dataSnapshotItr = dataSnapshot.getChildren().iterator();
            if (dataSnapshotItr.hasNext()) {
                contactDataSnapshot = dataSnapshotItr.next();
            }

            if (contactDataSnapshot != null) {

                System.out.println("Contact Data Snapshot: " + contactDataSnapshot);
                String recipientKey = contactDataSnapshot.child(KEY).getValue().toString();
                String phoneNumber = contactDataSnapshot.child(PHONE_NUMBER).getValue().toString();
                System.out.println("Via " + this.getClass().getSimpleName() + " - phoneNumber: " + phoneNumber + "; recipientKey: " + recipientKey);

                try {
                    postShopListViaFcm(context, phoneNumber, recipientKey, shopList);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    }

    public static void shareShopList(Context context, String contactPhoneNumber, ShopList shopList)  {

        Firebase.setAndroidContext(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(CONTACTS_FIREBASE_URL);
        ShopListSharingListener shopListSharingListener = new ShopListSharingListener(context, shopList);
        Query query = databaseReference.orderByChild(PHONE_NUMBER).startAt(contactPhoneNumber);
        query.addListenerForSingleValueEvent(shopListSharingListener);

    }

    public static void postShopListViaFcm(Context context, String phoneNumber, String recipientKey, ShopList shopList) throws IOException, JSONException {

        String firebaseTokenId = "";
        firebaseTokenId = PreferenceManager.getDefaultSharedPreferences(context).getString(FIREBASE_TOKEN, firebaseTokenId);
        System.out.println("Firebase Token ID: " + firebaseTokenId);

        String senderPhoneNumber = "";
        senderPhoneNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(PHONE_NUMBER, senderPhoneNumber);
        System.out.println("Sender Phone Number: " + senderPhoneNumber);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(shopList);
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        String shopListStr =  Base64.getEncoder().encodeToString(byteArray);

        JSONObject dataJsonObject = new JSONObject();
        dataJsonObject.put(TITLE, context.getString(R.string.firebase_cloud_message_title));
        dataJsonObject.put(MESSAGE, context.getString(R.string.firebase_cloud_message_message));
        dataJsonObject.put(PHONE_NUMBER, phoneNumber);
        dataJsonObject.put(SENDER_PHONE_NUMBER, senderPhoneNumber);
        dataJsonObject.put(SENDER_KEY, firebaseTokenId);
        dataJsonObject.put(SHOP_LIST_CONTENT, shopListStr);

        JSONObject mainJsonObject = new JSONObject();
        mainJsonObject.put(TO, recipientKey);
        mainJsonObject.put(DATA, dataJsonObject);

        String postPayload = mainJsonObject.toString();
        Uri fmcUri = Uri.parse(BuildConfig.FCM_POST_MESSAGE_URL).buildUpon().build();
        String authorizationKey = context.getString(R.string.firebase_cloud_messaging_server_key);

        RESTCommunication.setStrictMode();
        String postResponse = RESTCommunication.postPayload(fmcUri, authorizationKey, postPayload);

        System.out.print("FCM POST Response:\n" + postResponse);
        if (postResponse != null && !(postResponse = postResponse.trim()).isEmpty() && postPayload.indexOf(SUCCESS) > 0) {

            try {

                JSONObject jsonObject = new JSONObject(postPayload);
                if (jsonObject.getInt(SUCCESS) == 1) {
                    Toast.makeText(context, "Shop List \"" + shopList.getName() + "\" has been successfully shared.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to share Shop List \"" + shopList.getName() + "\".", Toast.LENGTH_LONG).show();
                }

            } catch (Throwable t) {
                Toast.makeText(context, "Error while sharing Shop List \"" + shopList.getName() + "\" due to " + t.getMessage() + ".", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        /*
        if(MainActivity.isAppRunning){
            //Some action
        }else{
            //Show notification as usual
        }
        */

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Map<String, String> remoteDataMap = remoteMessage.getData();
        String title = remoteDataMap.get(TITLE);
        String message = remoteDataMap.get(MESSAGE);
        String phoneNumber = remoteDataMap.get(PHONE_NUMBER);
        String senderPhoneNumber = remoteDataMap.get(SENDER_PHONE_NUMBER);
        String senderKey = remoteDataMap.get(SENDER_KEY);
        String shopListContent = remoteDataMap.get(SHOP_LIST_CONTENT);
        System.out.println("title: " + title);
        System.out.println("message: " + message);
        System.out.println("phoneNumber: " + phoneNumber);
        System.out.println("senderKey: " + senderKey);
        System.out.println("shopListContent: " + shopListContent);

        ShopList shopList = null;
        try {

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(shopListContent));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            shopList = (ShopList)objectInputStream.readObject();
            System.out.println("FCM Received Shop List: " + shopList);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (shopList != null) {

            SmartShopService smartShopService = new SmartShopService();
            ShopList foundShopList = smartShopService.getShopListByName(this, shopList.getName());
            if (foundShopList == null) {
                shopList.setShopListId(0);
                message = ("Shop List \"" + shopList.getName() + "\" has been shared by Contact# " + phoneNumber);
            } else {
                shopList.setShopListId(foundShopList.getShopListId());
                message = ("Shared Shop List \"" + shopList.getName() + "\" has been updated by Contact# " + phoneNumber);
            }

            smartShopService.addShopList(this, shopList);
            shopList = smartShopService.getShopListByName(this, shopList.getName());
            System.out.println("Received Shop List as SAVED locally: " + shopList);

            boolean linkShopList = false;
            Contact contact = smartShopService.findContactByPhoneNumber(this, senderPhoneNumber);
            if (contact == null) {

                System.out.println("Contact NOT FOUND");

                contact = new Contact(("Contact for Phone " + senderPhoneNumber), senderPhoneNumber, senderKey);
                smartShopService.addContact(this, contact);
                contact = smartShopService.findContactByPhoneNumber(this, senderPhoneNumber);
                linkShopList = true;

            } else {

                System.out.println("Contact FOUND");

                List<Long> contactIds = smartShopService.getContactIdsByShopList(this, shopList.getShopListId(), contact.getContactId());
                linkShopList = (contactIds == null || contactIds.isEmpty());

            }
            System.out.println("linkShopList: " + linkShopList);
            System.out.println("Contact: " + contact);

            if (linkShopList) {

                ShopListContact shopListContact = new ShopListContact(shopList.getShopListId(), contact.getContactId());
                smartShopService.addShopListContact(this, shopListContact);
				System.out.println("ShopList Contact: " + shopListContact);
				
            }

        }

        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

        //You should use an actual ID instead
        int notificationId = new Random().nextInt(60000);

        //Bitmap bitmap = getBitmapfromUrl(remoteDataMap.get("image-url"));

        Intent likeIntent = new Intent(this, LikeService.class);
        likeIntent.putExtra(NOTIFICATION_ID_EXTRA, notificationId);
        likeIntent.putExtra(IMAGE_URL_EXTRA, remoteDataMap.get("image-url"));
        PendingIntent likePendingIntent = PendingIntent.getService(this,
                notificationId + 1, likeIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel adminChannel = this.setupChannels();
            System.out.println("Channel set up successfully.\nAdmin Channel:\n" + adminChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                //.setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                        /*.setStyle(new NotificationCompat.BigPictureStyle()
                                .setSummaryText(remoteDataMap.get("message"))
                                .bigPicture(bitmap))/*Notification with Image*/
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(R.drawable.ic_favorite_true,
                        getString(R.string.notification_add_to_cart_button), likePendingIntent)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    private Bitmap getBitmapfromUrl(String imageUrl) {

        try {

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel setupChannels() {

        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }

        return adminChannel;

    }

}