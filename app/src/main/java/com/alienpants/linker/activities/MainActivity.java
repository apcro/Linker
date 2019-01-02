package com.alienpants.linker.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.alienpants.linker.R;
import com.alienpants.linker.libraries.Utilities;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        setContentView(R.layout.activity_main);

        mContext = this;

        Button play = findViewById(R.id.buttonPlay);
        play.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LevelsActivity.class);
            startActivity(intent);

        });

        Button exit = findViewById(R.id.buttonExit);
        exit.setOnClickListener(v -> finish());

        Button settings = findViewById(R.id.buttonSettings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utilities.hideUI(this);
    }
}
