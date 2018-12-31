package com.alienpants.linker.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.alienpants.linker.R;
import com.alienpants.linker.libraries.Utilities;
import com.wefika.flowlayout.FlowLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LevelsActivity_7x7 extends AppCompatActivity implements View.OnClickListener  {
    private int sizel;
    private int levell;

    private int numLevels = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_7x7);
        Utilities.hideUI(this);
        boolean isFileExist = false;
        try {
            FileInputStream fis = openFileInput("dataLevelSize.txt");
            if(fis != null)
                isFileExist = true;
            InputStreamReader isr = new InputStreamReader(fis);
            char[] dataChar = new char[100];
            String final_data = "";
            int size1;
            try {
                while((size1=isr.read(dataChar))>0)
                {
                    String read_data = String.copyValueOf(dataChar, 0, size1);
                    final_data += read_data;
                    dataChar = new char[100];

                }
                //Toast.makeText(getBaseContext(), "Contenu: " + final_data, Toast.LENGTH_LONG).show();
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

        makeButtons();

//        Button btnLevel_71 = findViewById(R.id.level_71);
//        Button btnLevel_72 = findViewById(R.id.level_72);
//        Button btnLevel_73 = findViewById(R.id.level_73);
        Button btnLevel_81 = findViewById(R.id.level_81);
        Button btnLevel_82 = findViewById(R.id.level_82);
        Button btnLevel_83 = findViewById(R.id.level_83);
        if(isFileExist) {
            if (levell == 1 && sizel == 7) {
//                btnLevel_72.setEnabled(false);
//                btnLevel_72.setAlpha(.5f);
//                btnLevel_72.setClickable(false);
//                btnLevel_73.setEnabled(false);
//                btnLevel_73.setAlpha(.5f);
//                btnLevel_73.setClickable(false);
                btnLevel_81.setEnabled(false);
                btnLevel_81.setAlpha(.5f);
                btnLevel_81.setClickable(false);
                btnLevel_82.setEnabled(false);
                btnLevel_82.setAlpha(.5f);
                btnLevel_82.setClickable(false);
                btnLevel_83.setEnabled(false);
                btnLevel_83.setAlpha(.5f);
                btnLevel_83.setClickable(false);
            } else if (levell == 2 && sizel == 7) {
//                btnLevel_73.setEnabled(false);
                btnLevel_81.setEnabled(false);
                btnLevel_82.setEnabled(false);
                btnLevel_83.setEnabled(false);
            } else if (levell == 3 && sizel == 7) {
                btnLevel_81.setEnabled(false);
                btnLevel_82.setEnabled(false);
                btnLevel_83.setEnabled(false);
            } else if (levell == 1 && sizel == 8) {
                btnLevel_82.setEnabled(false);
                btnLevel_83.setEnabled(false);
            } else if (levell == 2 && sizel == 8) {
                btnLevel_83.setEnabled(false);
            }
        }
        else{
//            btnLevel_72.setEnabled(false);
//            btnLevel_72.setAlpha(.5f);
//            btnLevel_72.setClickable(false);
//            btnLevel_73.setEnabled(false);
//            btnLevel_73.setAlpha(.5f);
//            btnLevel_73.setClickable(false);
            btnLevel_81.setEnabled(false);
            btnLevel_81.setAlpha(.5f);
            btnLevel_81.setClickable(false);
            btnLevel_82.setEnabled(false);
            btnLevel_82.setAlpha(.5f);
            btnLevel_82.setClickable(false);
            btnLevel_83.setEnabled(false);
            btnLevel_83.setAlpha(.5f);
            btnLevel_83.setClickable(false);
        }

//        btnLevel_71.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//                Bundle sizeRecu = new Bundle();
//                Bundle levelRecu = new Bundle();
//                sizeRecu.putInt("size", 7); // size
//                levelRecu.putInt("level", 1); // levle
//                intent.putExtras(sizeRecu);
//                intent.putExtras(levelRecu);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//        btnLevel_72.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//                Bundle sizeRecu = new Bundle();
//                Bundle levelRecu = new Bundle();
//                sizeRecu.putInt("size", 7); // size
//                levelRecu.putInt("level", 2); // levle
//                intent.putExtras(sizeRecu);
//                intent.putExtras(levelRecu);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//        btnLevel_73.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//                Bundle sizeRecu = new Bundle();
//                Bundle levelRecu = new Bundle();
//                sizeRecu.putInt("size", 7); // size
//                levelRecu.putInt("level", 3); // levle
//                intent.putExtras(sizeRecu);
//                intent.putExtras(levelRecu);
//                startActivity(intent);
//                finish();
//
//            }
//        });
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

    public void makeButtons() {
        int size = 7;

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (5 * scale + 0.5f);

        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT,
                FlowLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        FlowLayout levelsButtonLayout = findViewById(R.id.levelsButtonLayout);
        for(int i = 0 ; i < numLevels ; i++)
        {
            Button b = new Button(this);
            b.setText(String.valueOf(i+1));
            b.setTypeface(ResourcesCompat.getFont(this, R.font.snowdream), Typeface.BOLD);
            b.setTextColor(getColor(R.color.White));
            b.setBackgroundResource(R.drawable.ingame_counter);
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            b.setIncludeFontPadding(false);
            b.setLayoutParams(params);
            b.setId(i+1);
            b.setTag(size);
            b.setOnClickListener(this);

            levelsButtonLayout.addView(b);

        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        Bundle sizeRecu = new Bundle();
        Bundle levelRecu = new Bundle();
        sizeRecu.putInt("size", Integer.valueOf(String.valueOf(view.getTag()))); // size
        levelRecu.putInt("level", Integer.valueOf(String.valueOf(view.getId()))); // levle
        intent.putExtras(sizeRecu);
        intent.putExtras(levelRecu);
        startActivity(intent);
        finish();

    }
}
