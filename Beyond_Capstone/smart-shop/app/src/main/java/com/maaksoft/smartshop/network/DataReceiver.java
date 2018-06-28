package com.maaksoft.smartshop.network;

import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;

import android.content.Intent;

import android.os.IBinder;

import android.app.Service;

import com.maaksoft.smartshop.model.ShopList;

import com.maaksoft.smartshop.service.SmartShopService;

public class DataReceiver extends Service {

    public static String ipAddress;

    public final static String IP_ADDRESS = "ipAddress";

    public final static int PORT = 12345;

    private void connect(ServerSocket serverSocketListener) throws IOException {

        System.out.println("Waiting for Socket Client at " + serverSocketListener.getInetAddress().getHostAddress() + " ...");

        Socket socket = serverSocketListener.accept();
        System.out.println(String.format("Client connected from: %s", socket.getRemoteSocketAddress().toString()));
        /*
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream printStream = new PrintStream(socket.getOutputStream());

        StringBuilder outputStringBuilder = new StringBuilder();
        for (String inputLine; (inputLine = bufferedReader.readLine()) != null;) {
            outputStringBuilder.append(inputLine).append("\n");
        }
        System.out.println("Received Data:");
        System.out.println(outputStringBuilder);

        printStream.flush();
        printStream.close();
        bufferedReader.close();
        */

        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        try {

            ShopList shopList = (ShopList)objectInputStream.readObject();
            System.out.println("Networked Shop List:\n" + shopList);

            String copyShopListName = (shopList.getName() + " - Copy");
            SmartShopService smartShopService = new SmartShopService();
            ShopList shopListCopy = smartShopService.getShopListByName(getApplicationContext(), copyShopListName);
            if (shopListCopy != null) {
                shopList.setShopListId(shopListCopy.getShopListId());
            }
            shopList.setName(copyShopListName);

            smartShopService.addShopList(getApplicationContext(), shopList);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        //objectOutputStream.flush();
        //objectOutputStream.close();
        objectInputStream.close();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String ipAddress = intent.getStringExtra(IP_ADDRESS);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                ServerSocket serverSocketListener = null;
                try {
                    serverSocketListener = new ServerSocket(PORT, 20, InetAddress.getByName(ipAddress));
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

                if (serverSocketListener != null) {

                    while (true) {

                        try {
                            connect(serverSocketListener);
                        } catch (Throwable t) {

                            System.out.println(t.toString());

                            try {
                                connect(serverSocketListener);
                            } catch (Throwable t2) {
                                System.out.println("2. " + t2.toString());
                            }

                        }

                    }

                }

            }

        });

        thread.start();
        return Service.START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

}