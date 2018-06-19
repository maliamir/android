package com.android.smartshop.model;

import java.lang.reflect.Type;

import java.util.List;

import com.google.common.reflect.TypeToken;

import com.google.gson.Gson;

import android.arch.persistence.room.TypeConverter;

public class ListItemConverter {

    @TypeConverter
    public static List<ListItem> stringToListItems(String json) {
        Type type = new TypeToken<List<ListItem>>() {}.getType();
        return (new Gson()).fromJson(json, type);
    }

    @TypeConverter
    public static String listItemsToString(List<ListItem> list) {
        Type type = new TypeToken<List<ListItem>>() {}.getType();
        return (new Gson()).toJson(list, type);
    }

}