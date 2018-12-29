package com.alienpants.numberlink.data;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.alienpants.numberlink.models.Cell;

/**
 * Android
 * Created by cro on 27/12/2018.
 */
public class GameLevels {

    public static Cell[][] getGameLevel(Context context, int size, int level) {

        Cell[][] ArrayCell = new Cell[0][];
        
        if (size == 5) {
            GameLevel gameLevel = new GameLevel(context, "Level " + String.valueOf(level), level, size);

            switch (level) {
                case 1:
                    gameLevel.makeLayout("01..2.1.3......4.5..40523");
                    break;
                case 2:
                    gameLevel.makeLayout("0...20.3.21.......4.34..1");
                    break;
                case 3:
                    gameLevel.makeLayout("012340..34......567.15672");
                    break;
                case 4:
                    gameLevel.makeLayout("0..12330.2....1.5..44...5");
                    break;
                case 5:
                    gameLevel.makeLayout("01221......30.....3......");
                    break;
                case 6:
                    gameLevel.makeLayout("...11.2..0.2..3...44035.5");
                    break;
                case 7:
                    gameLevel.makeLayout("0..1234...34...0....1...2");
                    break;
                case 8:
                    gameLevel.makeLayout("0..123.0..4....4..5.3.152");
                    break;
                case 9:
                    gameLevel.makeLayout("0....1..1....2..033.2....");
                    break;
                case 10:
                    gameLevel.makeLayout("0..011....2.3.43.2......4");
                    break;
                case 11:
                    gameLevel.makeLayout("01..20...31...42....3...4");
                    break;
                case 12:
                    gameLevel.makeLayout("0...20.3....3...2.1..1...");
                    break;
            }
            ArrayCell = gameLevel.getGameLayout();
        }

        if (size == 6) {
            GameLevel gameLevel = new GameLevel(context, "Level " + String.valueOf(level), level, size);

            switch (level) {
                case 1:
                    gameLevel.makeLayout("....12.33.12.44.......50......056776");
                    break;
                case 2:
                    gameLevel.makeLayout("0....122..0.33..4.5..1..6..4..65....");
                    break;
                case 3:
                    gameLevel.makeLayout("012345.12.4...3.5.6.7..76.8..80.9..9");
                    break;
                case 4:
                    gameLevel.makeLayout("0012341..23.556.......78.....86499.7");
                    break;
                case 5:
                    gameLevel.makeLayout("012..30..3441....25....56....67....7");
                    break;
                case 6:
                    gameLevel.makeLayout("01..2.......3.....0.4....34..2.....1");
                    break;
                case 7:
                    gameLevel.makeLayout("..0123.1..2.....3.445.6.5.....7..706");
                    break;
                case 8:
                    gameLevel.makeLayout("0....011....2..3...4.5...6.23.465...");
                    break;
                case 9:
                    gameLevel.makeLayout("01....0...3.1..........3.45.2.245...");
                    break;
                case 10:
                    gameLevel.makeLayout("012340...34....2....55.6..1.......6.");
                    break;
                case 11:
                    gameLevel.makeLayout("0...220.3.4.1......5.3...166..5....4");
                    break;
                case 12:
                    gameLevel.makeLayout("01....01.3.2...4.3.5...4.2.6775....6");
                    break;
            }
            ArrayCell = gameLevel.getGameLayout();
        }


        if (size == 7) {
            GameLevel gameLevel = new GameLevel(context, "Level " + String.valueOf(level), level, size);

            switch (level) {
                case 1:
                    gameLevel.makeLayout("0123..40...45.1......2..6...........78.6..378...5");
                    break;
                case 2:
                    gameLevel.makeLayout("0..0123.45........2....1.3..........5.667....7..4");
                    break;
                case 3:
                    gameLevel.makeLayout("0...2330.4.........5..6.415......2....7..6881...7");
                    break;
                case 4:
                    gameLevel.makeLayout(".......1........2........2....3.4..54..35.1......");
                    break;
                case 5:
                    gameLevel.makeLayout("........6...7...7..43......1.....521364........52");
                    break;
                case 6:
                    gameLevel.makeLayout("...............63.3...5........156.14.24........2");
                    break;
                case 7:
                    gameLevel.makeLayout("...1234.50.....56........2...60.......1.7.3....74");
                    break;
                case 8:
                    gameLevel.makeLayout("0.1...34.1.5.34.2.5.......6..7.0....76..88......2");
                    break;
                case 9:
                    gameLevel.makeLayout("0......220.33.1...44.5..51..6..6...88...9.7..9..7");
                    break;
                case 10:
                    gameLevel.makeLayout("...11.2.3..0.4...5....53..6.......4......6027...7");
                    break;
                case 11:
                    gameLevel.makeLayout("0..110.22.334.55.4...6......7.....87..9..96.....8");
                    break;
                case 12:
                    gameLevel.makeLayout("0....012.......3..445.6..........77......5362...1");
                    break;
            }
            ArrayCell = gameLevel.getGameLayout();
        }
        
        return ArrayCell;
    }

}
