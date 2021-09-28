package com.example.facedetector;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class LCOFaceDetection extends Application {
    public final static String TEXT = "TEXT";
    public final static String NAME = "NAME";

    // initializing our firebase
    @Override
    public void onCreate()
    {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}

