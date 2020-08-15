package com.alienpants.linker.application;

import android.app.Application;
import android.content.Context;

import com.alienpants.linker.data.MyObjectBox;
import com.alienpants.linker.libraries.Backend;

import io.objectbox.BoxStore;

public class Linker extends Application  {

    public static final String preferencesFileName = "TakeMeAwayPreferences";

    private static Backend mBackend;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        mBackend = new Backend(this);

        boxStore = MyObjectBox.builder().androidContext(Linker.this).build();

    }

    public Linker() {
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
