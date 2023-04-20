package com.example.sevenalbum.activities.mainActivities;

import android.app.Application;

import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;

public class SevenAlbumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalDataManager.init(getApplicationContext());
    }
}
