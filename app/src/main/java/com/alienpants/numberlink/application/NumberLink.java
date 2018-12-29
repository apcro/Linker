package com.alienpants.numberlink.application;

import android.app.Application;
import android.content.Context;

import com.alienpants.numberlink.libraries.Backend;

public class NumberLink extends Application {

    public static final String preferencesFileName = "TakeMeAwayPreferences";
    public static final String MAINFONT = "fonts/Muli-Regular.ttf";
    public static final String HEADINGFONT = "fonts/Catamaran-Bold.ttf";

    public static final String DESTINATION_IMAGE_URI = "https://cdn1.takemeaway.io/images/destinations/";
    public static final String AVATAR_URI = "https://cdn1.takemeaway.io/images/user/avatars";

    private static Backend mBackend;

    @Override
    public void onCreate() {
        super.onCreate();
//        new FlurryAgent.Builder()
//                .withLogEnabled(true)
//                .withCaptureUncaughtExceptions(true)
//                .withLogLevel(Log.DEBUG)
//                .build(this, "TTN7BG86F595S6BM6HSC");


        mBackend = new Backend(this);

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

}
