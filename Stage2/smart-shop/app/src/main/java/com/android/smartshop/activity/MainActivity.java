package com.android.smartshop.activity;

import java.lang.reflect.Field;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;

import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import com.android.smartshop.fragments.SettingsFragment;
import com.android.smartshop.fragments.StoreListsFragment;
import com.android.smartshop.fragments.ShopListsFragment;
import com.android.smartshop.fragments.ShopListDetailFragment;

import com.android.smartshop.R;

public class MainActivity extends AppCompatActivity {

    private void disableShiftMode() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView)bottomNavigationView.getChildAt(0);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ShopListsFragment shopListsFragment = ShopListsFragment.newInstance();

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
                            selectedFragment = StoreListsFragment.newInstance();
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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, shopListsFragment);
        transaction.commit();

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        this.disableShiftMode();

    }

}