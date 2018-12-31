package com.alienpants.linker.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.alienpants.linker.R;
import com.alienpants.linker.libraries.Utilities;

public class MainActivity extends AppCompatActivity {

//    int data_block = 100;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        setContentView(R.layout.activity_main);

        mContext = this;

//        try {
//            FileInputStream fis = openFileInput("dataLevelSize.txt");
//            InputStreamReader isr = new InputStreamReader(fis);
//            char[] dataChar = new char[data_block];
//            String final_data = "";
//            int size1;
//            try {
//                while((size1=isr.read(dataChar))>0)
//                {
//                    String read_data = String.copyValueOf(dataChar, 0, size1);
//                    final_data += read_data;
//                    dataChar = new char[data_block];
//
//                }
//                //Toast.makeText(getBaseContext(),"Contenu: " + final_data, Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

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

}
