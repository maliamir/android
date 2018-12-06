package com.maaksoft.smartshop.dao;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.maaksoft.smartshop.model.Contact;

@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContact(Contact Contact);

    @Query("SELECT * FROM contacts")
    List<Contact> findContacts();

    @Query("SELECT * FROM contacts WHERE contactId=:contactId")
    Contact findContactById(long contactId);

    @Query("SELECT * FROM contacts WHERE name=:contactName")
    List<Contact> findContactByName(String contactName);

    @Query("SELECT * FROM contacts WHERE key=:key")
    Contact findContactByKey(String key);

    @Query("SELECT DISTINCT * FROM contacts WHERE phoneNumber=:phoneNumber")
    Contact findContactByPhoneNumber(String phoneNumber);

    @Query("DELETE FROM contacts WHERE contactId=:contactId")
    void deleteContact(long contactId);

}