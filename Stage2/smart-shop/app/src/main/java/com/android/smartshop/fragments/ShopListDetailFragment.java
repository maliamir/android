package com.android.smartshop.fragments;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.text.InputType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartshop.model.ListItem;
import com.android.smartshop.model.ShopList;
import com.android.smartshop.model.Setting;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.R;

public class ShopListDetailFragment extends Fragment {

    public static final String SHOP_LIST_ID = "SHOP_LIST_ID";

    private int defaultLimit = 1;
    private int defaultSortOrder = ListItem.SortType.HIGHEST_RAIINGS.getValue();

    private ShopList shopList;

    private SmartShopService smartShopService;

    private TextView itemNameTf;

    private Button addItemBtn;

    private TableLayout itemsListTbl;

    private TextView shopListNameTf;

    private Button saveListBtn;

    public static ShopListDetailFragment newInstance() {
        return new ShopListDetailFragment();
    }

    private ListItem.SortType getSortType(int checkboxId) {

        switch (checkboxId) {

            case R.id.lowest_price_attr:
                return ListItem.SortType.LOWEST_PRICE;

            default:
                return ListItem.SortType.HIGHEST_RAIINGS;

        }

    }

    private void toggleTable() {

        if (itemsListTbl.getChildCount() == 1) {
            itemsListTbl.setVisibility(View.GONE);
        } else {
            itemsListTbl.setVisibility(View.VISIBLE);
        }

    }

    private void populateTableRow(String itemName, int limit, int sortOrder) {

        //Inflate row "template" and fill out the fields.
        TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.table_row_for_items, null);
        tableRow.setBackgroundResource(R.drawable.cell_shape);
        ((TextView) tableRow.findViewById(R.id.item_name_attr)).setText(itemName);
        TextView limitTv = tableRow.findViewById(R.id.items_limit_attr);
        limitTv.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (limit > 0) {
            limitTv.setText(("" + limit));
        }

        RadioButton highestRatingsRadioButton = tableRow.findViewById(R.id.highest_ratings_attr);
        RadioButton lowestPriceRadioButton = tableRow.findViewById(R.id.lowest_price_attr);

        if (sortOrder == ListItem.SortType.HIGHEST_RAIINGS.getValue()) {
            highestRatingsRadioButton.setChecked(true);
            lowestPriceRadioButton.setChecked(false);
        } else {
            highestRatingsRadioButton.setChecked(false);
            lowestPriceRadioButton.setChecked(true);
        }

        ImageButton deleteItemBtn = tableRow.findViewById(R.id.deleteItemBtn);
        deleteItemBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                itemsListTbl.removeView(((TableRow) view.getParent()));
                toggleTable();
            }

        });

        this.itemsListTbl.addView(tableRow);
        this.itemsListTbl.requestLayout();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.items_list_detail, container, false);

        final Context context = getContext();
        this.smartShopService = new SmartShopService();

        final List<Setting> settings = this.smartShopService.getSettings(context);
        if (settings != null && settings.size() > 0) {
            Setting setting = settings.get(0);
            this.defaultLimit = setting.getLimit();
            this.defaultSortOrder = setting.getSortOrder();
        }

        this.itemNameTf = view.findViewById(R.id.item_name);
        this.addItemBtn = view.findViewById(R.id.add_item_btn);
        this.itemsListTbl = view.findViewById(R.id.items_list_tbl);
        this.shopListNameTf = view.findViewById(R.id.shop_list_name_et);
        this.saveListBtn = view.findViewById(R.id.save_list_btn);

        Bundle arguments = this.getArguments();
        if (arguments != null) {

            long shopListId = arguments.getLong(SHOP_LIST_ID);
            if (shopListId > 0) {

                this.shopList = this.smartShopService.getShopListById(context, shopListId);
                if (this.shopList != null) {

                    this.shopListNameTf.setText(this.shopList.getName());
                    List<ListItem> listItems = this.shopList.getListItems();
                    if (listItems != null && listItems.size() > 0) {

                        for (ListItem listItem : listItems) {
                            this.populateTableRow(listItem.getItemName(), listItem.getLimit(), listItem.getSortType());
                        }
                        this.itemsListTbl.setVisibility(View.VISIBLE);
                    }

                }

            }

        }

        this.addItemBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                populateTableRow(itemNameTf.getText().toString(), defaultLimit, defaultSortOrder);
                itemNameTf.setText("");
                toggleTable();
            }

        });

        this.saveListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int childCount = itemsListTbl.getChildCount();
                ArrayList<ListItem> listItems = new ArrayList<ListItem>();
                for (int index = 1; index < childCount; index++) {//Skipping index = 0 since it is a Table Row Header

                    View rowView = itemsListTbl.getChildAt(index);
                    if (rowView instanceof TableRow) {

                        TableRow tableRow = (TableRow) rowView;
                        String itemName = ((TextView) tableRow.findViewById(R.id.item_name_attr)).getText().toString();
                        if (itemName.isEmpty()) {
                            continue;
                        } else {

                            int checkedRadioButtonId = ((RadioGroup) tableRow.findViewById(R.id.items_sort_order_attr)).getCheckedRadioButtonId();


                            String limitStr = ((TextView) tableRow.findViewById(R.id.items_limit_attr)).getText().toString().trim();
                            int limit = 1;
                            if (!limitStr.isEmpty()) {
                                limit = Integer.parseInt(limitStr);
                            }

                            System.out.println("Table Row# " + (index + 1));
                            System.out.println("\titemName = " + itemName + "; limit = " + limit + "; checkedRadioButtonId = " + checkedRadioButtonId + "\n");

                            listItems.add(new ListItem(itemName, limit, getSortType(checkedRadioButtonId)));

                        }

                    }

                }

                if (listItems.isEmpty()) {
                    Toast.makeText(getContext(), ("At least 1 Item needs to be added to save a Shop List."), Toast.LENGTH_LONG).show();
                } else {

                    String name = shopListNameTf.getText().toString();
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Please provide a name to the Shop List.", Toast.LENGTH_LONG).show();
                    } else {

                        boolean isNew = false;
                        if (shopList == null) {
                            shopList = new ShopList(name, listItems);
                            isNew = true;
                        } else {
                            shopList.setName(name);
                            shopList.setListItems(listItems);
                        }

                        smartShopService.addShopList(context, shopList);

                        if (isNew) {
                            shopList = smartShopService.getShopListByName(context, shopList.getName());
                        } else {
                            shopList = smartShopService.getShopListById(context, shopList.getShopListId());
                        }

                        Toast.makeText(getContext(), ("Shop List \"" + shopList.getName() + "\" has been saved successfully."), Toast.LENGTH_LONG).show();
                        System.out.println("Shop List:\n" + shopList);

                    }

                }

            }

        });

        return view;

    }

}