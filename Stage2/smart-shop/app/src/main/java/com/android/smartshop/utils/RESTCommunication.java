package com.android.smartshop.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;

import org.apache.http.util.EntityUtils;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import android.os.StrictMode;

import android.widget.Toast;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public final class RESTCommunication {

    public static void setStrictMode() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public static boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if(connectivityManager != null) {
            //Check Mobile Data or WIFI Net is present
            isConnected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                           connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        }

        if (!isConnected) {
            Toast.makeText(context, "Not Connected to the Network/Internet. Please connect then reload the Screen.", Toast.LENGTH_LONG).show();
        }

        return isConnected;

    }

    public static String getJsonPayload(String uri) throws IOException {

        System.out.println("REST URI: " + uri);

        HttpResponse httpResponse = (new DefaultHttpClient()).execute(new HttpGet(uri));
        return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

    }

    public static String postPayload(Uri uri, String postPayload) throws IOException {

        System.out.println("REST URI: " + uri.toString());

        HttpPost httpPost = new HttpPost(uri.toString());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(postPayload, "UTF-8"));

        HttpResponse httpResponse = (new DefaultHttpClient()).execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

    }

}