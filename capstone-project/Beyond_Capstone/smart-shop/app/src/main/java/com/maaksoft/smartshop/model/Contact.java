package com.maaksoft.smartshop.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "contacts", indices = { @Index(value = "contactId") })
public class Contact {

    public final static String SELF = "self";

    @PrimaryKey(autoGenerate = true)
    private long contactId;

    private String name;
    private String phoneNumber;
    private String key;

    public Contact() {
    }

    @Ignore
    public Contact(String name, String phoneNumber, String key) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.key = key;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Store Details:\n");
        stringBuilder.append("\tName = " + name + "\n");
        stringBuilder.append("\tPhone Number = " + phoneNumber + "\n");
        stringBuilder.append("\tKey = " + key + "\n");

        return stringBuilder.toString();

    }

}