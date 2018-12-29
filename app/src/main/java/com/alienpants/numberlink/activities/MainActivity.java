package com.alienpants.numberlink.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alienpants.numberlink.R;
import com.alienpants.numberlink.libraries.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    int data_block = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        setContentView(R.layout.activity_main);
        try {
            FileInputStream fis = openFileInput("dataLevelSize.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            char[] dataChar = new char[data_block];
            String final_data = "";
            int size1;
            try {
                while((size1=isr.read(dataChar))>0)
                {
                    String read_data = String.copyValueOf(dataChar, 0, size1);
                    final_data += read_data;
                    dataChar = new char[data_block];

                }
                //Toast.makeText(getBaseContext(),"Contenu: " + final_data, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        Button btnLevels = findViewById(R.id.button_grille7x7);
//        Button btn8x8 = findViewById(R.id.button_grille8x8);
//        btn8x8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), LevelsActivity_8x8.class);
//                startActivity(intent);
//
//            }
//        });

        Button btn7 = findViewById(R.id.button7);
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
                startActivity(intent);

            }
        });
//        btn8x8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), LevelsActivity_8x8.class);
//                startActivity(intent);
//
//            }
//        });
        Button btnQuit = findViewById(R.id.button_exit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();

            }
        });


    }

//    public void goToLevels(View view) {
//        Intent intent = new Intent(getApplicationContext(), LevelsActivity.class);
//        startActivity(intent);
//    }
//
//    public void gameExit(View view) {
//     finish();
//    }
}
