package com.maaksoft.smartshop.network;

import com.maaksoft.smartshop.model.ShopList;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.Socket;

public class DataSender {

    public static String globalSenderIAddress;

    private String ipAddress;
    private int port;

    public DataSender(int port) {
        this.ipAddress = globalSenderIAddress;
        this.port = port;
    }

    public DataSender(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void sendString(String data) throws IOException {

        Socket socket = new Socket(this.ipAddress, this.port);
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        printWriter.println(data);
        printWriter.close();
        outputStream.flush();
        outputStream.close();
        socket.close();

    }

    public void sendShopeList(ShopList shopList) throws IOException {

        Socket socket = new Socket(this.ipAddress, this.port);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(shopList);
        objectOutputStream.flush();
        objectOutputStream.close();
        socket.close();

    }

}