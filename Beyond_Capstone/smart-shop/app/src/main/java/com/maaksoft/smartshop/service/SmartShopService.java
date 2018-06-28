package com.maaksoft.smartshop.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.maaksoft.smartshop.db.SmartShopDatabase;

import com.maaksoft.smartshop.model.Store;
import com.maaksoft.smartshop.model.Contact;
import com.maaksoft.smartshop.model.Setting;
import com.maaksoft.smartshop.model.ShopList;
import com.maaksoft.smartshop.model.ShopListContact;

import com.maaksoft.smartshop.messaging.FirebaseMessagingService;

public class SmartShopService {

    public void addShopList(Context context, ShopList shopList) {
        SmartShopDatabase.getDatabase(context).shopListDao().addShopList(shopList);
    }

    public List<ShopList> getShopLists(Context context) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopLists();
    }

    public ShopList getShopListById(Context context, long shopListId) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopListById(shopListId);
    }

    public ShopList getShopListByName(Context context, String name) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopListByName(name);
    }

    public void deleteShopList(Context context, long shopListId) {
        SmartShopDatabase.getDatabase(context).shopListDao().deleteShopList(shopListId);
    }

    public void addStore(Context context, Store store) {
        SmartShopDatabase.getDatabase(context).storeDao().addStore(store);
    }

    public List<Store> getStores(Context context) {
        return SmartShopDatabase.getDatabase(context).storeDao().findStores();
    }

    public Store findStoreByWalmartStoreId(Context context, int walmartStoreId) {
        return SmartShopDatabase.getDatabase(context).storeDao().findStoreByWalmartStoreId(walmartStoreId);
    }

    public void deleteStore(Context context, long storeId) {
        SmartShopDatabase.getDatabase(context).storeDao().deleteStore(storeId);
    }

    public void addSetting(Context context, Setting setting) {
        SmartShopDatabase.getDatabase(context).settingDao().addSetting(setting);
    }

    public List<Setting> getSettings(Context context) {
        return SmartShopDatabase.getDatabase(context).settingDao().findSettings();
    }

    public Setting getSettingById(Context context, long settingId) {
        return SmartShopDatabase.getDatabase(context).settingDao().findSettingById(settingId);
    }

    public void deleteSetting(Context context, long settingId) {
        SmartShopDatabase.getDatabase(context).settingDao().deleteSetting(settingId);
    }

    public void addContact(Context context, Contact contact) {
        SmartShopDatabase.getDatabase(context).contactDao().addContact(contact);
    }

    public List<Contact> getContacts(Context context) {
        return SmartShopDatabase.getDatabase(context).contactDao().findContacts();
    }

    public Contact getContactById(Context context, long contactId) {
        return SmartShopDatabase.getDatabase(context).contactDao().findContactById(contactId);
    }

    public List<Contact> getContactByName(Context context, String name) {
        return SmartShopDatabase.getDatabase(context).contactDao().findContactByName(name);
    }

    public Contact findContactByKey(Context context, String key) {
        return SmartShopDatabase.getDatabase(context).contactDao().findContactByKey(key);
    }

    public Contact findContactByPhoneNumber(Context context, String phoneNumber) {
        return SmartShopDatabase.getDatabase(context).contactDao().findContactByPhoneNumber(phoneNumber);
    }

    public void deleteContact(Context context, long contactId) {
        SmartShopDatabase.getDatabase(context).contactDao().deleteContact(contactId);
    }

    public String shareShopListWithAllContacts(Context contex, ShopList shopList) {

        List<Long> contactIds = this.getContactIdsByShopList(contex, shopList.getShopListId());
        System.out.println("No. of Contacts found: " + contactIds.size() + "; contactIds: " + contactIds);
        if (contactIds != null && contactIds.size() > 0) {

            List<Contact> contacts = new ArrayList<Contact>();
            for (Long contactId: contactIds) {

                Contact contact = this.getContactById(contex, contactId);
                if (contact != null) {
                    contacts.add(contact);
                }

            }

            return this.shareShopListWithContacts(contex, shopList, contacts, false);

        } else {
            return "";
        }

    }

    public String shareShopListWithContacts(Context contex, ShopList shopList, List<Contact> contacts, boolean addContact) {

        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("No. of Contacts found: " + contacts.size() + "; contacts: " + contacts);
        if (contacts != null && contacts.size() > 0) {

            for (Contact contact : contacts) {

                if (!contact.getName().equals(Contact.SELF)) {

                    boolean isShared = false;
                    try {

                        FirebaseMessagingService.shareShopList(contex, contact.getPhoneNumber(), shopList);
                        stringBuilder.append("\t").append(contact.getPhoneNumber()).append(" ").append(" successful.").append("\n");
                        isShared = true;

                    } catch (Throwable t) {
                        stringBuilder.append("\t").append(contact.getPhoneNumber()).append(" ").append(" failed.").append("\n");
                    }

                    if (addContact && isShared) {

                        ShopListContact shopListContact = new ShopListContact(shopList.getShopListId(), contact.getContactId());
                        this.addShopListContact(contex, shopListContact);

                    }

                }

            }

        }

        String info = stringBuilder.toString().trim();
        if (!info.isEmpty()) {
            info = ("Shop List \"" + shopList.getName() + "\" Sharing Status with Contact(s):\n" + info);
        }

        return info;

    }

    public String shareShopListWithContacts(Context contex, ShopList shopList, List<Contact> contacts) {
        return this.shareShopListWithContacts(contex, shopList, contacts,true);
    }

    public void addShopListContact(Context context, ShopListContact shopListContact) {
        SmartShopDatabase.getDatabase(context).shopListContactsDao().addShopListContact(shopListContact);
    }

    public List<Long> getContactIdsByShopList(Context context, long shopListId) {
        return SmartShopDatabase.getDatabase(context).shopListContactsDao().findContactIdsByShopList(shopListId);
    }

    public List<Long> getContactIdsByShopList(Context context, long shopListId, long contactId) {
        return SmartShopDatabase.getDatabase(context).shopListContactsDao().findShopListContacts(shopListId, contactId);
    }

    public void deleteContactsByShopList(Context context, long shopListId) {
        SmartShopDatabase.getDatabase(context).shopListContactsDao().deleteContactsByShopList(shopListId);
    }

}