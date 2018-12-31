package com.alienpants.linker.application;

import android.app.Application;
import android.content.Context;

import com.alienpants.linker.data.MyObjectBox;
import com.alienpants.linker.libraries.Backend;

import io.objectbox.BoxStore;

public class NumberLink extends Application  {

    public static final String preferencesFileName = "TakeMeAwayPreferences";
    public static final String MAINFONT = "fonts/Muli-Regular.ttf";
    public static final String HEADINGFONT = "fonts/Catamaran-Bold.ttf";

    public static final String DESTINATION_IMAGE_URI = "https://cdn1.takemeaway.io/images/destinations/";
    public static final String AVATAR_URI = "https://cdn1.takemeaway.io/images/user/avatars";

    private static Backend mBackend;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
//        new FlurryAgent.Builder()
//                .withLogEnabled(true)
//                .withCaptureUncaughtExceptions(true)
//                .withLogLevel(Log.DEBUG)
//                .build(this, "TTN7BG86F595S6BM6HSC");


        mBackend = new Backend(this);

        boxStore = MyObjectBox.builder().androidContext(NumberLink.this).build();

    }

    public NumberLink() {
        super();
    }

    public static Backend getBackend() {
        return mBackend;
    }

    public static Context context() {
        return mBackend.getApplication().getApplicationContext();
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

}
