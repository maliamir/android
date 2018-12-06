package com.android.smartshop.utils;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.smartshop.model.Product;
import com.android.smartshop.model.ItemInfo;

public class JsonProcessor {

    public static LinkedList<ItemInfo> loadItemInfos(String jsonPayload) {

        LinkedList<ItemInfo> itemInfos = new LinkedList<ItemInfo>();
        try {

            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray itemsJsonArray = jsonObject.getJSONObject("itemsSearched").getJSONArray("items");

            if (itemsJsonArray != null && itemsJsonArray.length() > 0) {

                for (int i = 0; i < itemsJsonArray.length(); i++) {

                    JSONObject itemJsonObject = (JSONObject)itemsJsonArray.get(i);
                    String itemName = itemJsonObject.getString("itemName");

                    JSONArray productsJsonArray = itemJsonObject.getJSONObject("productsFound").getJSONArray("items");
                    if (productsJsonArray != null && productsJsonArray.length() > 0) {

                        LinkedList<Product> products = new LinkedList<Product>();
                        for (int j = 0; j < productsJsonArray.length(); j++) {

                            JSONObject productJsonObject = productsJsonArray.getJSONObject(j);
                            Product product = new Product(productJsonObject.getString("name"), productJsonObject.getString("location"),
                                                          productJsonObject.getDouble("price"), productJsonObject.getDouble("ratings"),
                                                          productJsonObject.getString("imageUrl"));
                            products.add(product);

                        }

                        itemInfos.add(new ItemInfo(itemName, products));

                    }

                }

            }

        } catch (JSONException je) {
            je.printStackTrace();
        }

        return itemInfos;

    }

}