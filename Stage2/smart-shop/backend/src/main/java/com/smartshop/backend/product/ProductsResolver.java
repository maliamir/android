package com.smartshop.backend.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.smartshop.backend.product.model.Product;

/**
 * Class to fetch matching Products using Walmart REST API and sort them either by lowest price or highest ratings.
 */
public final class ProductsResolver {

    private static final int PAGE_SIZE = 50;
    private static final String BASE_URL = "http://search.mobile.walmart.com/search";

    private static Product getProductInstance(JSONObject productJsonObject) throws JSONException {

        Integer section = -1;
        Long price = new Long(-1);
        String aisle = "N/A";

        JSONObject locationObject = productJsonObject.getJSONObject("location");
        try {

            JSONArray aisles = locationObject.getJSONArray("aisle");
            if (aisles.length() > 0) {
                aisle = (String)aisles.get(0);
            }

            JSONArray detailed = locationObject.getJSONArray("detailed");
            if (detailed.length() > 0) {
                section = (Integer)((JSONObject)detailed.get(0)).get("section");
            }

            JSONObject priceObj = productJsonObject.getJSONObject("price");
            price = priceObj.getLong("priceInCents");

        } catch (JSONException je) {
            return null;
        }

        if (aisle.equals("N/A") || price < 0 || section < 0) {
            return null;
        }

        Double ratings = productJsonObject.getJSONObject("ratings").getDouble("rating");
        return new Product(productJsonObject.getString("name"), aisle, section, price, ratings,
                           productJsonObject.getJSONObject("images").getString("thumbnailUrl"));

    }

    private static String getJsonPayload(String urlString) throws IOException {

        String jsonPayload = null;
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                jsonPayload = scanner.next();
            }

        } finally {
            urlConnection.disconnect();
        }

        return jsonPayload;

    }

    /**
     * Fetch matching Products using Walmart REST API:<br>
     * &emsp;&emsp;&emsp;- That are "In Stock"&emsp;&emsp;- Assigned Aisle location&emsp;&emsp;- Assigned Price
     * @param storeId Store Id where Items need to be searched.
     * @param itemName ItemsEnvelope to be searched.
     * @return List of matching Products.
     * @throws IOException
     */
    public static ArrayList<Product> getProducts(String storeId, String itemName) throws IOException {

        int offset = 0, totalResultsCount = 0;
        ArrayList<Product> products = new ArrayList<Product>();
        do {

            String urlWithParams = (BASE_URL + "?store=" + storeId + "&query=" + URLEncoder.encode(itemName) + "&size=" + PAGE_SIZE + "&offset=" + offset);
            System.out.println("URL with Parameters: " + urlWithParams);

            String jsonPayload = getJsonPayload(urlWithParams);
            if (jsonPayload != null &&  !(jsonPayload = jsonPayload.trim()).isEmpty()) {

                JSONArray jsonArray = null;
                try {

                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    totalResultsCount = jsonObject.getInt("totalCount");
                    jsonArray = jsonObject.getJSONArray("results");

                } catch (JSONException je) {
                    je.printStackTrace();
                }

                if (jsonArray != null && jsonArray.length() > 0) {

                    for (int index = 0; index < jsonArray.length(); index++) {

                        try {

                            JSONObject productObj = jsonArray.getJSONObject(index);
                            String status = (String)productObj.getJSONObject("inventory").get("status");
                            if (status.equalsIgnoreCase("In Stock")) {

                                Product product = getProductInstance(jsonArray.getJSONObject(index));
                                if (product != null) {
                                    products.add(product);
                                }

                            }

                        } catch (JSONException je) {
                            //je.printStackTrace();
                        }

                    }

                }
                offset += PAGE_SIZE;

            }

        } while (offset <= totalResultsCount);


        return products;

    }

    public static void main(String[] args) throws Exception {

        ArrayList<Product> products = ProductsResolver.getProducts("2505", "toilet paper");
        Collections.sort(products, ProductComparator.getComparator(ProductComparator.LOWEST_PRICE_COMPARATOR_TYPE));
        System.out.println( + products.size() + " Products FOUND.");
        System.out.println(products);

    }

}