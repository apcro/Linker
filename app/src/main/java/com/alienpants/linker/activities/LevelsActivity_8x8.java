package com.alienpants.linker.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alienpants.linker.R;
import com.alienpants.linker.libraries.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LevelsActivity_8x8 extends AppCompatActivity {
    private int sizel;
    private int levell;
    boolean isFileExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_8x8);
        Utilities.hideUI(this);
        /* Lire le fichier text */
        try {
            FileInputStream fis = openFileInput("dataLevelSize.txt");
            if (fis != null)
                isFileExist = true;
            InputStreamReader isr = new InputStreamReader(fis);
            char[] dataChar = new char[100];
            String final_data = "";
            int size1;
            try {
                while ((size1 = isr.read(dataChar)) > 0) {
                    String read_data = String.copyValueOf(dataChar, 0, size1);
                    final_data += read_data;
                    dataChar = new char[100];

                }
                // Toast.makeText(getBaseContext(), "Contenu: " + final_data, Toast.LENGTH_LONG).show();
                String[] splitedStr = final_data.split(";");
                String sz = splitedStr[0];
                String lvl = splitedStr[1];
                sizel = Integer.valueOf(sz);
                levell = Integer.valueOf(lvl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button btnLevel_81 = findViewById(R.id.level_g8_81);
        Button btnLevel_82 = findViewById(R.id.level_g8_82);
        Button btnLevel_83 = findViewById(R.id.level_g8_83);

        if (isFileExist) {

            if ((levell == 1 || levell == 2 || levell == 3) && sizel == 7) {
                btnLevel_82.setEnabled(false);
                btnLevel_82.setAlpha(.5f);
                btnLevel_82.setClickable(false);
                btnLevel_83.setEnabled(false);
                btnLevel_83.setAlpha(.5f);
                btnLevel_83.setClickable(false);
            } else if (levell == 1 && sizel == 8) {
                btnLevel_82.setEnabled(false);
                btnLevel_82.setAlpha(.5f);
                btnLevel_82.setClickable(false);
                btnLevel_83.setEnabled(false);
                btnLevel_83.setAlpha(.5f);
                btnLevel_83.setClickable(false);
            } else if (levell == 2 && sizel == 8) {
                btnLevel_83.setEnabled(false);
                btnLevel_83.setAlpha(.5f);
                btnLevel_83.setClickable(false);
            }
        } else {
            btnLevel_81.setClickable(false);
            btnLevel_82.setEnabled(false);
            btnLevel_82.setAlpha(.5f);
            btnLevel_82.setClickable(false);
            btnLevel_83.setEnabled(false);
            btnLevel_83.setAlpha(.5f);
            btnLevel_83.setClickable(false);
        }


        btnLevel_81.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle sizeRecu = new Bundle();
                Bundle levelRecu = new Bundle();
                sizeRecu.putInt("size", 8); // size
                levelRecu.putInt("level", 1); // levle
                intent.putExtras(sizeRecu);
                intent.putExtras(levelRecu);
                startActivity(intent);
                finish();

            }
        });
        btnLevel_82.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle sizeRecu = new Bundle();
                Bundle levelRecu = new Bundle();
                sizeRecu.putInt("size", 8); // size
                levelRecu.putInt("level", 2); // levle
                intent.putExtras(sizeRecu);
                intent.putExtras(levelRecu);
                startActivity(intent);
                finish();

            }
        });
        btnLevel_83.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle sizeRecu = new Bundle();
                Bundle levelRecu = new Bundle();
                sizeRecu.putInt("size", 8); // size
                levelRecu.putInt("level", 3); // levle
                intent.putExtras(sizeRecu);
                intent.putExtras(levelRecu);
                startActivity(intent);
                finish();

            }
        });

    }

}
