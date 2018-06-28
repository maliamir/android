package com.maaksoft.smartshop.dao;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.maaksoft.smartshop.model.Setting;

@Dao
public interface SettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSetting(Setting Setting);

    @Query("SELECT * FROM settings")
    List<Setting> findSettings();

    @Query("SELECT * FROM settings WHERE settingId=:settingId")
    Setting findSettingById(long settingId);

    @Query("DELETE FROM settings WHERE SettingId=:SettingId")
    void deleteSetting(long SettingId);

}