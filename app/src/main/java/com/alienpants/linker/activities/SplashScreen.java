package com.alienpants.linker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.alienpants.linker.application.Linker;
import com.alienpants.linker.data.LevelData;
import com.alienpants.linker.data.LevelData_;
import com.alienpants.linker.libraries.Utilities;
import com.alienpants.linker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class SplashScreen extends Activity {

//    private static final String TAG = SplashScreen.class.getSimpleName();

    private Box mLevelsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mLevelsBox = ((Linker) getApplication()).getBoxStore().boxFor(LevelData.class);
        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, 5).and().equal(LevelData_.num, 1).build().find();

        if (levels.size() == 0) {
            resetLevels();
        }

        Utilities.hideUI(this);

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, SPLASH_TIME_OUT);
    }

    public void startMainActivity() {

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void resetLevels() {
        // load levels
        BufferedReader reader;

        int size = 0;
        int num = 0;
        LevelData level = new LevelData(0, 0);

        List<LevelData> levels = new ArrayList<>();

        try {
            final InputStream file = getAssets().open("levelpack1.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null) {
                char first = line.charAt(0);
                if (first == ':') {
                    char second = line.charAt(1);
                    // new mTableSize
                    size = Character.getNumericValue(second);
                    num = 1;
                } else {
                    level = new LevelData(size, num);
                    level.setLayout(line);

                    if (size == 5 && num == 1) {
                        level.setLocked(false);
                    }

                    levels.add(level);
                    num++;
                }
                line = reader.readLine();
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }

        for (LevelData thisLevel : levels) {
            mLevelsBox.put(thisLevel);
        }
    }

}
