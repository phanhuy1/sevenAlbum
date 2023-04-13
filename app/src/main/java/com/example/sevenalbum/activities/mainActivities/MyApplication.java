package com.example.sevenalbum.activities.mainActivities;

import android.app.Application;

import com.example.sevenalbum.activities.mainActivities.data_favor.DataLocalManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
    }
}
