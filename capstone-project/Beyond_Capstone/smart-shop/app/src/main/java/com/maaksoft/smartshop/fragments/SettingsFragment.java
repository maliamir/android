package com.maaksoft.smartshop.fragments;

import java.util.List;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.maaksoft.smartshop.R;

import com.maaksoft.smartshop.model.ListItem;
import com.maaksoft.smartshop.model.Setting;

import com.maaksoft.smartshop.service.SmartShopService;

public class SettingsFragment extends Fragment {

    private Setting setting;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings, container, false);

        final Context context = getContext();

        final EditText defaultLimitEditText = view.findViewById(R.id.default_limit_et);
        final RadioButton highestRatingsRadioButton = view.findViewById(R.id.highest_ratings_setting);
        final RadioButton lowestPriceRadioButton = view.findViewById(R.id.lowest_price_setting);

        final SmartShopService smartShopService = new SmartShopService();
        final List<Setting> settings = smartShopService.getSettings(context);
        if (settings != null && settings.size() > 0) {

            this.setting = settings.get(0);

            defaultLimitEditText.setText(("" + setting.getLimit()));

            if (this.setting.getSortOrder() == ListItem.SortType.HIGHEST_RAIINGS.getValue
                    ()) {
                highestRatingsRadioButton.setChecked(true);
                lowestPriceRadioButton.setChecked(false);
            } else {
                highestRatingsRadioButton.setChecked(false);
                lowestPriceRadioButton.setChecked(true);
            }

        }

        Button saveSettingsBTn = view.findViewById(R.id.save_settings_btn);
        saveSettingsBTn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                boolean sortOrderNotSelected = (!highestRatingsRadioButton.isChecked() && !lowestPriceRadioButton.isChecked());
                String message = "";

                int limit = 1;
                String limitStr = defaultLimitEditText.getText().toString().trim();
                if (limitStr.isEmpty()) {
                    message += ("\n\t" + getString(R.string.input_limit_value_message));
                } else {
                    limit = Integer.parseInt(limitStr);
                }

                if (sortOrderNotSelected) {
                    message += ("\n\t" + getString(R.string.select_sort_order_message));
                }

                if (message.isEmpty()) {

                    if (setting == null) {
                        setting = new Setting();
                    }

                    int sortOrder = highestRatingsRadioButton.isChecked() ? ListItem.SortType.HIGHEST_RAIINGS.getValue() : ListItem.SortType.LOWEST_PRICE.getValue();

                    setting.setLimit(limit);
                    setting.setSortOrder(sortOrder);

                    smartShopService.addSetting(context, setting);
                    Toast.makeText(getContext(), getString(R.string.settings_saved_message), Toast.LENGTH_LONG).show();
                    System.out.println(setting);

                } else {
                    message = ("Input Error(s):" + message);
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }

            }

        });

        return view;

    }

}