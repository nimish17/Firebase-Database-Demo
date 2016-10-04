package com.firebase;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;


public class AppController extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
    }

}