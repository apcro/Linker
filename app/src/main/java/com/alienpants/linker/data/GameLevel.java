package com.alienpants.linker.data;

import android.content.Context;
import android.graphics.Color;

import com.alienpants.linker.R;
import com.alienpants.linker.models.Cell;

import androidx.core.content.ContextCompat;

/**
 * Android
 * Created by cro on 27/12/2018.
 */
public class GameLevel {


    private Context mContext;
    private int size;

    private Cell[][] gameLayout;

    public GameLevel(Context context, String levelName, int levelNumber, int size) {
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
                gameLayout[row][col] = new Cell(mContext, Cell.CellShape.Circle, Color.TRANSPARENT, row, (i % size), false);;
            } else {
                int colour = getColourFromKey(layout.charAt(i));
                gameLayout[row][col] = new Cell(mContext, Cell.CellShape.Circle, ContextCompat.getColor(mContext, colour), row, (i % size), true);
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
                return  R.color.BurlyWood;
            case 'a':
                return  R.color.Fuchsia;
            case 'b':
                return  R.color.DarkGreen;
            case 'c':
                return  R.color.Honeydew;
            case 'd':
                return  R.color.LawnGreen;
            case 'e':
                return  R.color.CadetBlue;
            case 'f':
                return  R.color.DarkSeaGreen;
            case 'g':
                return  R.color.PaleVioletRed;
            case 'h':
                return  R.color.DeepSkyBlue;
            case 'i':
                return  R.color.Moccasin;
            case 'j':
                return  R.color.Thistle;
            case 'k':
                return  R.color.Silver;
            case 'l':
                return  R.color.Navy;
            case 'm':
                return  R.color.Yellow;
            case 'n':
                return  R.color.Plum;
            case 'o':
                return  R.color.LightPink;
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
        return R.color.BrandWhite;
    }
}
