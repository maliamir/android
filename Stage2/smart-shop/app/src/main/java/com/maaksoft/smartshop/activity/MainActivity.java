package com.maaksoft.smartshop.activity;

import java.lang.reflect.Field;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;

import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.maaksoft.smartshop.fragments.SettingsFragment;
import com.maaksoft.smartshop.fragments.StoreListsFragment;
import com.maaksoft.smartshop.fragments.ShopListsFragment;
import com.maaksoft.smartshop.fragments.ShopListDetailFragment;

import com.maaksoft.smartshop.BuildConfig;
import com.maaksoft.smartshop.R;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";

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

        AdView adView = findViewById(R.id.ad_view);

        try {

            MobileAds.initialize(this, BuildConfig.SMART_SHOP_AD_MOB_APP_ID);
            adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        } catch (Throwable throwable) {

            throwable.printStackTrace();
            System.out.println(throwable.getClass() + " has occurred.");
            adView.setVisibility(View.GONE);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.loadAd();

        final StoreListsFragment storesFragment = StoreListsFragment.newInstance();
        final ShopListsFragment shopListsFragment = ShopListsFragment.newInstance();
        Fragment fragment = shopListsFragment;

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {

                    case R.id.add_shop_list_action:
                        selectedFragment = ShopListDetailFragment.newInstance();
                        break;

                    case R.id.shop_lists_action:
                        selectedFragment = shopListsFragment;
                        break;

                    case R.id.stores_actions:
                        selectedFragment = storesFragment;
                        break;

                    case R.id.settings_action:
                        selectedFragment = SettingsFragment.newInstance();

                        break;

                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;

            }

        });

        Intent intent = getIntent();
        Integer fragmentIndex = 1;
        if (intent != null) {

            fragmentIndex = intent.getIntExtra(FRAGMENT_INDEX, 1);
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