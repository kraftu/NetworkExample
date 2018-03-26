package ru.tinkoff.network.example;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by kraftu on 26.03.2018.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
