package com.alienpants.numberlink.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alienpants.numberlink.R;
import com.alienpants.numberlink.application.NumberLink;
import com.alienpants.numberlink.data.GameLevel;
import com.alienpants.numberlink.data.GameLevels;
import com.alienpants.numberlink.libraries.Backend;
import com.alienpants.numberlink.libraries.Utilities;
import com.alienpants.numberlink.models.Cell;
import com.wefika.flowlayout.FlowLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LevelsActivity extends AppCompatActivity implements View.OnClickListener {

    int levelCount = 12;
    ArrayList<Cell[][]> mLevels;
    Cell[][] mLevel;
    ArrayList<String> mSizes;

    Backend mBackend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Utilities.hideUI(this);

        mBackend = NumberLink.getBackend();

        // let's cheat a lot for build purposes
        mLevels = new ArrayList<>();
        for (int i = 5; i <= 7; i++) {
            for (int j = 1; j <= levelCount; j++) {

                mLevel = GameLevels.getGameLevel(this, i, j);
                mLevels.add(mLevel);
            }
        }

        TextView labelView = new TextView(this);

        LinearLayout container = findViewById(R.id.layoutHolder);

        // 5x5
        labelView.setText("5x5 Levels");
        labelView.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.BOLD);
        labelView.setTextColor(getColor(R.color.White));
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        container.addView(labelView);
        makeButtons(5, container);

        labelView = new TextView(this);
        labelView.setText("6x6 Levels");
        labelView.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.BOLD);
        labelView.setTextColor(getColor(R.color.White));
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        container.addView(labelView);
        makeButtons(6, container);

        labelView = new TextView(this);
        labelView.setText("7x7 Levels");
        labelView.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.BOLD);
        labelView.setTextColor(getColor(R.color.White));
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        container.addView(labelView);
        makeButtons(7, container);




    }

    public void makeButtons(int size, LinearLayout container) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (2 * scale + 0.5f);

        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT,
                FlowLayout.LayoutParams.WRAP_CONTENT
        );

        FlowLayout levelsButtonLayout = new FlowLayout(this);

        FlowLayout.LayoutParams buttonParams = new FlowLayout.LayoutParams(250, 250);
        buttonParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        for(int i = 0 ; i < levelCount ; i++)
        {
            Button b = new Button(this);
            b.setText(String.valueOf(i+1));
            b.setWidth(86);
            b.setHeight(86);
            b.setTypeface(ResourcesCompat.getFont(this, R.font.snowdream), Typeface.NORMAL);
            b.setTextColor(getColor(R.color.BrandDarkBlue));
            b.setBackgroundResource(getStars(size, i));
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            b.setIncludeFontPadding(false);
            b.setLayoutParams(buttonParams);
            b.setId(i+1);
            b.setTag(size);
            b.setShadowLayer(2f, 1.6f,1.6f, getColor(R.color.White));
            if (!isLocked(size, i)) {
                b.setOnClickListener(this);
            } else {
                b.setAlpha(0.5f);
            }

            levelsButtonLayout.addView(b);

        }

        layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        levelsButtonLayout.setLayoutParams(layoutParams);
        container.addView(levelsButtonLayout);

    }

    public int getStars(int size, int level) {
        String scores = mBackend.getSharedPreferences(size+"x"+size);
        if (scores == null) {
            scores = getSetScores(size, level);
        }
        String[] separated = scores.split(":");
        String[] scores_moves = separated[level].split(";");
        switch (scores_moves[0]) {
            case "1":
                return R.drawable.number_circle_1;
            case "2":
                return R.drawable.number_circle_2;
            case "3":
                return R.drawable.number_circle_3;
            default:
                return R.drawable.number_circle_0;
        }
    }

    private String getSetScores(int size, int level) {
        String scores = mBackend.getSharedPreferences(size+"x"+size);
        if (scores == null) {
            scores = "";
            for (int i = 0; i < levelCount; i++) {
                scores = scores + ".;.:";
            }
            mBackend.setSharedPreferences(size+"x"+size, scores);
            return "0;0";
        }
        String[] separated = scores.split(":");
        if (separated[level] != ".;.") {
            return separated[level];
        } else {
            return "0;0";
        }
    }

    public boolean isLocked(int size, int level) {
        String scores = mBackend.getSharedPreferences(size+"x"+size);
        String[] separated = scores.split(":");
        if (level == 0) {
            return false;
        } else {
            if (separated[level].equals(".;.") && !separated[(level-1)].equals(".;.")) {
                return false;
            } else {
                return true;
            }
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
