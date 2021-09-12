package com.josalla.store.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AppService extends Service {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        preferences = getSharedPreferences("MASTEKA", MODE_PRIVATE);
        editor = preferences.edit();

        super.onTaskRemoved(rootIntent);
        //here you will get call when app close.
        editor.putBoolean("main_open", true);
        editor.putString("popupImage", "");
        editor.commit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}