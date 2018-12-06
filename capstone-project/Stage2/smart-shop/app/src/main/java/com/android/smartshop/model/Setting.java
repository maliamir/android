package com.android.smartshop.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "settings", indices = { @Index(value = "settingId") })
public class Setting {

    @PrimaryKey(autoGenerate = true)
    private int settingId;

    private int limit;
    private int sortOrder;

    public Setting() {
    }

    @Ignore
    public  Setting(int limit, int sortOrder) {
        this.limit = limit;
        this.sortOrder = sortOrder;
    }

    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Setting Details:\n");
        stringBuilder.append("\tId = " + settingId + "\n");
        stringBuilder.append("\tLimit = " + limit + "\n");
        stringBuilder.append("\tSort Order = " + sortOrder + "\n");

        return stringBuilder.toString();

    }

}