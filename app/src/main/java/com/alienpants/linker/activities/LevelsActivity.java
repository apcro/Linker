package com.alienpants.linker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import io.objectbox.Box;

import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alienpants.linker.R;
import com.alienpants.linker.application.Linker;
import com.alienpants.linker.data.LevelData;
import com.alienpants.linker.data.LevelData_;
import com.alienpants.linker.libraries.Backend;
import com.alienpants.linker.libraries.Utilities;
import com.alienpants.linker.models.Cell;
import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class LevelsActivity extends AppCompatActivity implements View.OnClickListener {

    int levelCount = 12;
    ArrayList<Cell[][]> mLevels;
    Cell[][] mLevel;
    ArrayList<String> mSizes;

    Backend mBackend;
    Context mContext;
    Box mLevelsBox;

    public Integer[] levelSizes = {5,6,7,8,9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Utilities.hideUI(this);

        mBackend = Linker.getBackend();
        mContext = this;
        mLevelsBox = ((Linker) getApplication()).getBoxStore().boxFor(LevelData.class);

        TextView labelView;

        LinearLayout container = findViewById(R.id.layoutHolder);

        for (Integer sizeNum : levelSizes) {
            labelView = new TextView(this);
            labelView.setText(sizeNum + "x" + sizeNum + " Levels");
            labelView.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.BOLD);
            labelView.setTextColor(getColor(R.color.White));
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            container.addView(labelView);
            makeButtons(sizeNum, container);
        }

        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(view -> onBackPressed());

    }

    public void makeButtons(int size, LinearLayout container) {

        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, size).build().find();

        int numLevels = levels.size();

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (2 * scale + 0.5f);

        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT,
                FlowLayout.LayoutParams.WRAP_CONTENT
        );

        FlowLayout levelsButtonLayout = new FlowLayout(this);

        FlowLayout.LayoutParams buttonParams = new FlowLayout.LayoutParams(250, 250);
        buttonParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        for(int i = 1 ; i <= numLevels ; i++) {
            Button b = new Button(this);
            b.setText(String.valueOf(i));
            b.setWidth(86);
            b.setHeight(86);
            b.setTypeface(ResourcesCompat.getFont(this, R.font.snowdream), Typeface.NORMAL);
            b.setTextColor(getColor(R.color.BrandDarkBlue));
            b.setBackgroundResource(getStars(levels.get(i-1).getScore()));
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            b.setIncludeFontPadding(false);
            b.setLayoutParams(buttonParams);
            b.setId(i);
            b.setTag(size);
            b.setShadowLayer(2f, 1.6f,1.6f, getColor(R.color.White));
            if (!isLocked(levels.get(i-1))) {
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

    public int getStars(int score) {

        switch (score) {
            case 1:
                return R.drawable.number_circle_1;
            case 2:
                return R.drawable.number_circle_2;
            case 3:
                return R.drawable.number_circle_3;
            default:
                return R.drawable.number_circle_0;
        }
    }

    public boolean isLocked(LevelData level) {
        return level.getLocked();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra("mTableSize", Integer.valueOf(String.valueOf(view.getTag())));
        intent.putExtra("mCurrentLevel", Integer.valueOf(String.valueOf(view.getId())));
        startActivity(intent);
        finish();
    }

}
