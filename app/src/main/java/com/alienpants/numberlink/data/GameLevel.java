package com.alienpants.numberlink.data;

import android.content.Context;
import android.graphics.Color;

import com.alienpants.numberlink.R;
import com.alienpants.numberlink.models.Cell;

import androidx.core.content.ContextCompat;

/**
 * Android
 * Created by cro on 27/12/2018.
 */
public class GameLevel {


    private Context mContext;
    private String levelName;
    private int levelNumber;
    private int size;

    private Cell[][] gameLayout;

    public GameLevel(Context context, String levelName, int levelNumber, int size) {
        this.levelName = levelName;
        this.levelNumber = levelNumber;
        this.gameLayout = new Cell[size][size];
        this.size = size;
        this.mContext = context;
    }

    public void makeLayout(String layout) {
        int row = 0;
        int col = 0;
        for (int i = 0; i < layout.length(); i++) {
            char cell = layout.charAt(i);
            if (cell == '.') {
                gameLayout[row][col] = new Cell(mContext, Cell.CellShape.Circle, Cell.CellType.None, Color.TRANSPARENT, row, (i % size), false);;
            } else {
                int colour = getColourFromKey(layout.charAt(i));
                gameLayout[row][col] = new Cell(mContext, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(mContext, colour), row, (i % size), true);
            }
            col++;
            if (((i+1) % size) == 0) {
                row++;
                col = 0;
            }
        }
    }

    public Cell[][] getGameLayout() {
        return  this.gameLayout;
    }

    // the slow way for now
    private int getColourFromKey(char key) {
        switch(key) {
            case '0':
                return R.color.RoyalBlue;
            case '1':
                return  R.color.Red;
            case '2':
                return  R.color.Yellow;
            case '3':
                return  R.color.Orange;
            case '4':
                return  R.color.LimeGreen;
            case '5':
                return  R.color.PaleTurquoise;
            case '6':
                return  R.color.Gray;
            case '7':
                return  R.color.PaleGreen;
            case '8':
                return  R.color.BlueViolet;
            case '9':
                return  R.color.Red;
            case 'a':
                return  R.color.Red;
            case 'b':
                return  R.color.Red;
            case 'c':
                return  R.color.Red;
            case 'd':
                return  R.color.Red;
            case 'e':
                return  R.color.Red;
            case 'f':
                return  R.color.Red;
            case 'g':
                return  R.color.Red;
            case 'h':
                return  R.color.Red;
            case 'i':
                return  R.color.Red;
            case 'j':
                return  R.color.Red;
            case 'k':
                return  R.color.Red;
            case 'l':
                return  R.color.Red;
            case 'm':
                return  R.color.Red;
            case 'n':
                return  R.color.Red;
            case 'o':
                return  R.color.Red;
            case 'p':
                return  R.color.Red;
            case 'q':
                return  R.color.Red;
            case 'r':
                return  R.color.Red;
            case 's':
                return  R.color.Red;
            case 't':
                return  R.color.Red;
            case 'u':
                return  R.color.Red;
            case 'v':
                return  R.color.Red;
            case 'w':
                return  R.color.Red;
            case 'x':
                return  R.color.Red;
            case 'y':
                return  R.color.Red;
            case 'z':
                return  R.color.Red;
        }
        return R.color.White;
    }
}
