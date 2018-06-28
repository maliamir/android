package com.maaksoft.smartshop.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.provider.Telephony;

import android.telephony.SmsMessage;

import com.maaksoft.smartshop.R;

public class SmsListener extends BroadcastReceiver {

    public final static String SMS_MESSAGE_SENDER_IP_KEY = "SMS_MESSAGE_SENDER_IP_KEY";
    public final static String SMS_MESSAGE_RECEIVED_FILTER_NAME = "Sms.Message.Received";

    @Override
    public void onReceive(Context context, Intent intent) {

        String appBasedSmsPrefix = (context.getString(R.string.app_name) + ":");
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){

            Bundle bundle = intent.getExtras();
            if (bundle != null){

                try {

                    String ipAddress = "";
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for(int index = 0; index < pdus.length; index++){

                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdus[index]);
                        String msgBody = smsMessage.getMessageBody();
                        if (msgBody.indexOf(appBasedSmsPrefix) >= 0) {

                            String[] msgInfo = msgBody.split("\n");
                            ipAddress = msgInfo[1].split(":")[1].trim();
                            System.out.println("IP Address: " + ipAddress);
                            break;

                        }

                    }

                    if (!ipAddress.isEmpty()) {
                        Intent intentCall = new Intent(SMS_MESSAGE_RECEIVED_FILTER_NAME);
                        intentCall.putExtra(SMS_MESSAGE_SENDER_IP_KEY, ipAddress);
                        context.sendBroadcast(intentCall);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        }

    }

}