package com.alienpants.linker.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.alienpants.linker.R;
import com.alienpants.linker.application.NumberLink;
import com.alienpants.linker.data.LevelData;
import com.alienpants.linker.libraries.Backend;
import com.alienpants.linker.libraries.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.objectbox.Box;

public class SettingsActivity extends AppCompatActivity {

    Backend mBackend;
    Context mContext;
    Box mLevelsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Utilities.hideUI(this);

        mBackend = NumberLink.getBackend();
        mContext = this;

        mLevelsBox = ((NumberLink) getApplication()).getBoxStore().boxFor(LevelData.class);

        Button reset = findViewById(R.id.buttonReset);
        reset.setOnClickListener(view -> resetAll());

        Button back = findViewById(R.id.button_back);
        back.setOnClickListener(view -> onBackPressed());
    }

    public void resetAll() {
        resetDatabase();
        resetLevels();
    }

    public void resetDatabase() {

        // delete all scores
        mLevelsBox.removeAll();

    }

    public void resetLevels() {
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
                    // new size
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
