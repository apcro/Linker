package com.alienpants.numberlink.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;


import com.alienpants.numberlink.application.NumberLink;
import com.alienpants.numberlink.data.GameLevel;
import com.alienpants.numberlink.libraries.Backend;
import com.alienpants.numberlink.libraries.Utilities;
import com.alienpants.numberlink.models.Cell;
import com.alienpants.numberlink.R;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;


public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();

    LinearLayout mTableHolder;
    TableLayout mGameTable;
    int mGameTableWidth, mGameTableHeight;
    int mCellWidth, mCellHeight;

    Backend mBackend;

    private static final long MIN_DELAY_MS = 100;
    private long mLastClickTime;

    // TODO: to replace size and level from level Activity


    int size = 7;
    int level = 1;
    int levelCount = 12;
    Cell[][] mTableCellsArray, ArrayCellTwo; // Table that contains the cells of the game
    ArrayList<Cell> mCellsUsed = new ArrayList<>();
    int mGameScore = 0;
    int mMoveScore = 0;
    int mBestScore = 0;
    boolean active_draw = false;
    int mColourChosen;
    int IndexPreviousCellRow = 0;
    int IndexPreviousCellCol = 0;
    int IndexCurrentCellRow = 0;
    int IndexCurrentCellCol = 0;
    int mCurrentTouchedRow, mCurrentTouchedCol;
    Cell previous_cell;
    Cell current_cell;
    boolean over = false; // Partie perdue
    AlertDialog.Builder alert;
    AlertDialog.Builder mExitAlert;
    AlertDialog.Builder mGameOverAlert;
    Dialog mWinAlert;
    FileOutputStream fileOutputStream;
    OutputStreamWriter osw;

    Context mContext;

    private ImageView mTouch;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Utilities.hideUI(this);

        mContext = this;

        mBackend = NumberLink.getBackend();


        Bundle sizeRecu = getIntent().getExtras();
        if (sizeRecu != null) {
            size = sizeRecu.getInt("size");
        }
        Bundle levelRecu = getIntent().getExtras();
        level = levelRecu.getInt("level");

        // get scores, and set them if needed
        String scores = getSetScores(size, level);
        String[] scores_moves = scores.split(";");
        mBestScore = Integer.parseInt(scores_moves[1]);

        mGameTable = findViewById(R.id.table_game);
        mTableHolder = findViewById(R.id.tableHolder);

        mGameTableWidth = 0;
        mGameTableHeight = 0;
        mCellWidth = 0;
        mCellHeight = 0;

        // Fill the table according to the level and the size passed in parameters
        InitialiseNewTabelCellsArray(size, level);
        BuildTable(size);

        // Show victory message
        mWinAlert = new Dialog(GameActivity.this);
        mWinAlert.setContentView(R.layout.dialog_success);

        Button yes = mWinAlert.findViewById(R.id.btn_yes);
        Button no = mWinAlert.findViewById(R.id.btn_no);
        yes.setOnClickListener(view -> {
            saveData();
            goNextLevel();
            mWinAlert.dismiss();
            Utilities.hideUI(this);
        });
        no.setOnClickListener(view -> {
            resetGame();
            saveData();
            mWinAlert.dismiss();
            Utilities.hideUI(this);
        });

        // Show message to exit the game
        mExitAlert = new AlertDialog.Builder(mContext);
        mExitAlert.setTitle("Exit Game");
        mExitAlert.setMessage("Do you want to leave the game");
        mExitAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();   // Exit the application and return to the window to choose new
                            // levels
            }
        });
        mExitAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                return; // Do nothing and continue to play
            }
        });

        // Show message for lost party

        mGameOverAlert = new AlertDialog.Builder(mContext);
        mGameOverAlert.setTitle("Game over");
        mGameOverAlert.setMessage("Do you want to play again");
        mGameOverAlert.setPositiveButton("Yes", (dialog, id) -> resetGame());
        mGameOverAlert.setNegativeButton("Leave the game", (dialog, id) -> finish());

        // Buttons to initialize and exit the game
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(v -> resetGame());

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        Button buttonExit = findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(v -> mExitAlert.show());

        mTouch = findViewById(R.id.touchCircle);
        mTouch.setX(-100);
        mTouch.setY(-100);
        mTouch.setVisibility(View.GONE);

        setMoves();
        setScore();

        mGameTable.setOnTouchListener((v, event) -> {
            // Receiving TableLayout Parameters
            mGameTable.setMeasureWithLargestChildEnabled(true);

            if (mGameTableHeight == 0) {
                setMeasurements();
            }

            // position of the box pressed
            int IndexRow = (int) event.getY() / mCellWidth;
            int IndexCol = (int) event.getX() / mCellHeight;

            mCurrentTouchedRow = IndexRow;
            mCurrentTouchedCol = IndexCol;

            // Check that the IndexRow and IndexCol will not exceed the limits
            IndexRow = IndexRow > (size - 1) ? (size - 1) : IndexRow;
            IndexCol = IndexCol > (size - 1) ? (size - 1) : IndexCol;

            // Once a cell is touched it is the ActionDown event that is captured
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if (mTableCellsArray[IndexRow][IndexCol].getColor() != Color.TRANSPARENT && mTableCellsArray[IndexRow][IndexCol].isUsed()) {
                    IndexPreviousCellRow = IndexRow;
                    IndexPreviousCellCol = IndexCol;
                    IndexCurrentCellRow = IndexRow;
                    IndexCurrentCellCol = IndexCol;
                    active_draw = true;
                    mColourChosen = mTableCellsArray[IndexRow][IndexCol].getColor();

                    if (!isConnected(mTableCellsArray[IndexRow][IndexCol])) {
                        mTableCellsArray[IndexRow][IndexCol].setType(Cell.CellType.First);
                        addCellUsed(mTableCellsArray[IndexRow][IndexCol], true);
                        previous_cell = mCellsUsed.get(mCellsUsed.size() - 1);
                        current_cell = mCellsUsed.get(mCellsUsed.size() - 1);
                    }
                }

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {


                // hide touch circle
                mTouch.setX(-100);
                mTouch.setY(-100);
                mTouch.setVisibility(View.GONE);

                mMoveScore++;
                setMoves();

                if (mTableCellsArray[IndexRow][IndexCol].getColor() != mColourChosen || !mTableCellsArray[IndexRow][IndexCol].isUsed()) {
                    clearDrawnCell();
                } else {
                    if (!isConnected(mTableCellsArray[IndexRow][IndexCol])) {
                        mTableCellsArray[IndexRow][IndexCol].setType(Cell.CellType.Second);
                        redesignSecondCircle(IndexCurrentCellRow, IndexCurrentCellCol);
                        // Update the number of tubes
                        if (isConnectingLineValid()) {
                            mGameScore++;
                        } else {
                            clearDrawnCell();
                        }

                    }
                    mCellsUsed.clear();

                    setScore();
                }

                // check if the game is won
                if (isTableComplete()) {
                    saveData();
                    if (level == 3 && size == 8) {
                        mGameOverAlert.setMessage("You have completed all levels!");
                        mGameOverAlert.show();
                    }
                    Objects.requireNonNull(mWinAlert.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                    mWinAlert.show();
                }

                // check if the game is unfinished
                if (isGameOver()) {
                    mGameOverAlert.show();
                }


            }

//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                applyTilt(event.getRawX(), event.getRawY());
//            }

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {

                // show touch circle
                mTouch.setX(event.getRawX() - 96 - 24);
                mTouch.setY(event.getRawY() - 96 - 24);

                ImageViewCompat.setImageTintList(mTouch, ColorStateList.valueOf(mColourChosen));

                mTouch.setVisibility(View.VISIBLE);

                if (mCellsUsed.size() > 0 && (mTableCellsArray[IndexRow][IndexCol].getColor() == Color.TRANSPARENT || mTableCellsArray[IndexRow][IndexCol].isUsed())) {
                    if (!addCellUsed(mTableCellsArray[IndexRow][IndexCol], false)) {
                        active_draw = false;
                    }
                }
                if (mCellsUsed.size() > 1) {
                    previous_cell = mCellsUsed.get(mCellsUsed.size() - 2);
                    current_cell = mCellsUsed.get(mCellsUsed.size() - 1);
                    IndexPreviousCellRow = previous_cell.getIndexRow();
                    IndexPreviousCellCol = previous_cell.getIndexCol();
                    IndexCurrentCellRow = current_cell.getIndexRow();
                    IndexCurrentCellCol = current_cell.getIndexCol();

                }

                // Draw lines between circles
                DrawLine();

                return true;
            }
            return false;
        });

    }

    public void resetData() {

//        try {
//            fileOutputStream = openFileOutput("dataLevelSize.txt", Context.MODE_PRIVATE);
//            osw = new OutputStreamWriter(fileOutputStream);
//            try {
//                osw.write(7 + ";" + 1);
//                osw.flush();
//                osw.close();
//                Toast.makeText(getBaseContext(), "Level saved", Toast.LENGTH_LONG).show();
//                int data_block = 100;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
    private void saveData() {

        String scores = mBackend.getSharedPreferences(size+"x"+size);
        if (scores == null) {
            scores = "";
            for (int i = 0; i < levelCount; i++) {
                scores = scores + ".;.:";
            }
        }
        String[] separated = scores.split(":");
        separated[(level-1)] = String.valueOf(mGameScore)+";"+String.valueOf(mMoveScore);

        String result = TextUtils.join(":", separated);

        mBackend.setSharedPreferences(size+"x"+size, result);


//        try {
//            fileOutputStream = openFileOutput("dataLevelSize.txt", Context.MODE_PRIVATE);
//            osw = new OutputStreamWriter(fileOutputStream);
//            try {
//                osw.write(size + ";" + level);
//                osw.flush();
//                osw.close();
//                Toast.makeText(getBaseContext(), "Level saved", Toast.LENGTH_LONG).show();
//                int data_block = 100;
//                try {
//                    FileInputStream fis = openFileInput("dataLevelSize.txt");
//                    InputStreamReader isr = new InputStreamReader(fis);
//                    char[] dataChar = new char[data_block];
//                    String final_data = "";
//                    int size1;
//                    try {
//                        while((size1 = isr.read(dataChar))>0) {
//                            String read_data = String.copyValueOf(dataChar, 0, size1);
//                            final_data+=read_data;
//                            dataChar = new char[data_block];
//
//                        }
//                        //Toast.makeText(getBaseContext(),"Contenu: " + final_data, Toast.LENGTH_LONG).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


    }

    // As soon as a cell is traversed by two colors, this function returns True, that goes
    // will define if the game is successful or not
    private boolean isOver() {

        for (int i = 0; i < mCellsUsed.size() - 1; i++) {
            if ((mCellsUsed.get(i).getIndexRow() - mCellsUsed.get(i + 1).getIndexRow() > 1) ||
                    (mCellsUsed.get(i).getIndexCol() - mCellsUsed.get(i + 1).getIndexCol() > 1)) {
                over = true;
            }
        }

        return over;
    }

    // Function that checks if the game has been won
    private boolean isTableComplete() {
        boolean success = true;

        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                // any empty slots, not finished
//                if (!mTableCellsArray[row][col].isUsed()) {
//                    success = false;
//                }
                if (mTableCellsArray[row][col].getColor() == Color.TRANSPARENT) {
                    success = false;
                }
                // any unconnected starts/ends next to each other
                if (mTableCellsArray[row][col].getCellShape() == Cell.CellShape.Circle) {
                    success = false;
                }
            }
        }

        return (success && !isOver());
    }

    // Function that checks if the game has been lost
    private boolean isGameOver() {
        boolean game_over = false;
        int number_first_circle = 0;
        int number_seconde_circle = 0;
        int number_used_circle = 0;
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                if (mTableCellsArray[row][col].isUsed()) {
                    ++number_used_circle;
                    if (mTableCellsArray[row][col].getType() == Cell.CellType.First) {
                        ++number_first_circle;
                    } else if (mTableCellsArray[row][col].getType() == Cell.CellType.Second) {
                        ++number_seconde_circle;
                    }
                }
            }
        }

        if ((number_first_circle == number_seconde_circle) && (number_first_circle == number_used_circle / 2))
            game_over = true;
        return game_over && !isTableComplete();
    }

    // Function that checks if two circles of the same colors are connected
    private boolean isConnected(Cell cell) {
        boolean connected = false;
        if (cell.getType() != Cell.CellType.None && cell.isUsed()) {
            for (int row = 0; row < size; ++row) {
                for (int col = 0; col < size; ++col) {
                    if (cell.getType() == Cell.CellType.First) {
                        if (cell.getColor() == mTableCellsArray[row][col].getColor() && mTableCellsArray[row][col].getType() == Cell.CellType.Second) {
                            connected = true;
                        }
                    } else {
                        if (cell.getColor() == mTableCellsArray[row][col].getColor() && mTableCellsArray[row][col].getType() == Cell.CellType.First) {
                            connected = true;
                        }
                    }
                }
            }
        }
        return connected;

    }

    // Adds the cells that are being drawn
    private boolean addCellUsed(Cell cell_to_add, boolean isFirst) {
        boolean exist = false;
        if (!isConnected(cell_to_add)) {
            for (Cell item : mCellsUsed) {
                if (item.getIndexRow() == cell_to_add.getIndexRow() && item.getIndexCol() == cell_to_add.getIndexCol()) {
                    exist = true;
                }
            }
            if (!exist) {
                mCellsUsed.add(cell_to_add);
            }
            return true;
        } else {
            return false;
        }
    }

    // Check if the circle is a final circle
    private boolean isSecondCircle(int IndexRow, int IndexCol) {
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                if (mTableCellsArray[row][col].getColor() == mTableCellsArray[IndexRow][IndexCol].getColor() && mTableCellsArray[row][col].getType() == Cell.CellType.First) {
                    return true;
                }
            }
        }
        return false;
    }

    // redraw circles according to direction
    private void redesignSecondCircle(int IndexRow, int IndexCol) {
        // From Up to Down
        if (IndexPreviousCellRow > IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
            if (mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].isUsed() && mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].getCellShape() == Cell.CellShape.Circle) {
                mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].setCellShape(Cell.CellShape.CircleDown);
            }
        }

        // From Down to Up
        if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol == IndexCol) {
            if (mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].isUsed() && mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].getCellShape() == Cell.CellShape.Circle) {
                mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].setCellShape(Cell.CellShape.CircleUp);
            }
        }

        // From Right to Left
        if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
            if (mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].isUsed() && mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].getCellShape() == Cell.CellShape.Circle) {
                mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].setCellShape(Cell.CellShape.CircleRight);
            }
        }

        // From Left to Right
        if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
            if (mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].isUsed() && mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].getCellShape() == Cell.CellShape.Circle) {
                mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].setCellShape(Cell.CellShape.CircleLeft);
            }
        }


        clearTableLayout();
        BuildTable(size);
    }

    // trace the corners
    private void DrawLine() {

        // temporary cells to check if we have a corners
        Cell cell_a = null;
        Cell cell_b = null;
        Cell cell_c = null;

        // to have a corner, you need at least 3 cells chosen
        if (mCellsUsed.size() >= 3) {
            cell_a = mCellsUsed.get(mCellsUsed.size() - 3);
            cell_b = mCellsUsed.get(mCellsUsed.size() - 2);
            cell_c = mCellsUsed.get(mCellsUsed.size() - 1);

        }

        // Verify if there is a corner
        if ((mCellsUsed.size() >= 3) && (cell_a.getIndexRow() != cell_c.getIndexRow() && cell_a.getIndexCol() != cell_c.getIndexCol())) {
            Log.d(TAG, "DrawLine Cell corner draw");
            // From Left to Down or From Down to Left
            if (((cell_c.getIndexRow() == cell_b.getIndexRow()) && (cell_a.getIndexCol() ==
                    cell_b.getIndexCol()) && (cell_a.getIndexCol() > cell_c.getIndexCol()) &&
                    (cell_a.getIndexRow() > cell_c.getIndexRow())) || ((cell_a.getIndexRow() ==
                    cell_b.getIndexRow()) && (cell_c.getIndexCol() == cell_b.getIndexCol()) &&
                    (cell_a.getIndexCol() < cell_c.getIndexCol()) && (cell_a.getIndexRow() <
                    cell_c.getIndexRow()))) {

                if (!mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].isUsed()) {
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setCellShape(Cell.CellShape.LeftDown);
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setColor(mColourChosen);
                    clearTableLayout();
                    BuildTable(size);
                }
            }

            // From Left to Up or From Up to Left
            if (((cell_a.getIndexRow() == cell_b.getIndexRow()) && (cell_c.getIndexCol() ==
                    cell_b.getIndexCol()) && (cell_a.getIndexCol() < cell_c.getIndexCol()) &&
                    (cell_a.getIndexRow() > cell_c.getIndexRow())) || ((cell_c.getIndexRow() ==
                    cell_b.getIndexRow()) && (cell_a.getIndexCol() == cell_b.getIndexCol()) &&
                    (cell_a.getIndexCol() > cell_c.getIndexCol()) && (cell_a.getIndexRow() <
                    cell_c.getIndexRow()))) {

                if (!mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].isUsed()) {
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setCellShape(Cell.CellShape.LeftUp);
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setColor(mColourChosen);
                    clearTableLayout();
                    BuildTable(size);
                }
            }

            // From Right to Down or From Down to Right
            if (((cell_a.getIndexRow() == cell_b.getIndexRow()) && (cell_c.getIndexCol() ==
                    cell_b.getIndexCol()) && (cell_a.getIndexCol() > cell_c.getIndexCol()) &&
                    (cell_a.getIndexRow() < cell_c.getIndexRow())) || ((cell_c.getIndexRow() ==
                    cell_b.getIndexRow()) && (cell_a.getIndexCol() == cell_b.getIndexCol()) &&
                    (cell_a.getIndexCol() < cell_c.getIndexCol()) && (cell_a.getIndexRow() >
                    cell_c.getIndexRow()))) {

                if (!mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].isUsed()) {
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setCellShape(Cell.CellShape.RightDown);
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setColor(mColourChosen);
                    clearTableLayout();
                    BuildTable(size);
                }
            }

            // From Right to Up or From Up to Right
            if (((cell_a.getIndexRow() == cell_b.getIndexRow()) && (cell_c.getIndexCol() ==
                    cell_b.getIndexCol()) && (cell_a.getIndexCol() > cell_c.getIndexCol()) &&
                    (cell_a.getIndexRow() > cell_c.getIndexRow())) || ((cell_c.getIndexRow() ==
                    cell_b.getIndexRow()) && (cell_a.getIndexCol() == cell_b.getIndexCol()) &&
                    (cell_a.getIndexCol() < cell_c.getIndexCol()) && (cell_a.getIndexRow() <
                    cell_c.getIndexRow()))) {

                if (!mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].isUsed()) {
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setCellShape(Cell.CellShape.RightUp);
                    mTableCellsArray[cell_b.getIndexRow()][cell_b.getIndexCol()].setColor(mColourChosen);
                    clearTableLayout();
                    BuildTable(size);
                }
            }

        } else {
            Log.d(TAG, "DrawLine Cell exit draw");

            // From Down to Up
            if (IndexPreviousCellRow > IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.UpDown);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleUp);
                }
            }

            // From Up to Down
            if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.UpDown);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleDown);
                }
            }

            // From Right to Left
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftRight);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleLeft);
                }
            }

            // From Left to Right
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftRight);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleRight);
                }
            }
            // From Down to Right
            if (IndexPreviousCellRow > IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.RightDown);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleUp);
                }
            }

            // From Up to Right
            if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.RightUp);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleDown);
                }
            }

            // From Down to Left
            if (IndexPreviousCellRow > IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftDown);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleLeft);
                }

            }

            // From Up to Left
            if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftUp);
                if (mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].isUsed() && mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].getCellShape() == Cell.CellShape.Circle) {
                    mTableCellsArray[IndexPreviousCellRow][IndexPreviousCellCol].setCellShape(Cell.CellShape.CircleRight);
                }
            }
        }

        if (mTableCellsArray[IndexCurrentCellRow][IndexCurrentCellCol].getColor() == Color.TRANSPARENT) {
            Log.d(TAG, "DrawLine Cell entry draw");
            // entry?
            if (IndexPreviousCellRow > IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.DownHalf);
            }

            // From Up to Down
            if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.UpHalf);
            }

            // From Right to Left
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.RightHalf);
            }

            // From Left to Right
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftHalf);
            }

        }
    }

    private void clearDrawnCell() {
        for (Cell item : mCellsUsed) {
            int IndexCol = item.getIndexCol();
            int IndexRow = item.getIndexRow();
            if (!(mTableCellsArray[IndexRow][IndexCol].getCellShape() == Cell.CellShape.Circle ||
                    mTableCellsArray[IndexRow][IndexCol].getCellShape() == Cell.CellShape.CircleUp ||
                    mTableCellsArray[IndexRow][IndexCol].getCellShape() == Cell.CellShape.CircleDown ||
                    mTableCellsArray[IndexRow][IndexCol].getCellShape() == Cell.CellShape.CircleRight ||
                    mTableCellsArray[IndexRow][IndexCol].getCellShape() == Cell.CellShape.CircleLeft)) {

                mTableCellsArray[IndexRow][IndexCol].setColor(Color.TRANSPARENT);
                mTableCellsArray[IndexRow][IndexCol].setUsed(false);
            }
            mTableCellsArray[IndexRow][IndexCol].setCellShape(Cell.CellShape.Circle);
            mTableCellsArray[IndexRow][IndexCol].setType(Cell.CellType.None);
        }
        mCellsUsed.clear();
        BuildTable(size);
    }

    // trace the tubes
    private void _drawLine(int IndexRow, int IndexCol, int color_chosen, boolean active, Cell.CellShape cellShape) {
        if (active_draw) {
            if (!mTableCellsArray[IndexRow][IndexCol].isUsed()) {
                if (cellShape == Cell.CellShape.UpDown && (IndexRow != 0 && IndexRow != size - 1)) {
                    mTableCellsArray[IndexRow][IndexCol].setCellShape(cellShape);
                    mTableCellsArray[IndexRow][IndexCol].setColor(color_chosen);
                    BuildTable(size);
                } else if (cellShape == Cell.CellShape.LeftRight && (IndexCol != 0 && IndexCol != size - 1)) {

                    mTableCellsArray[IndexRow][IndexCol].setCellShape(cellShape);
                    mTableCellsArray[IndexRow][IndexCol].setColor(color_chosen);
                    BuildTable(size);
                }
            } else if (isSecondCircle(IndexRow, IndexCol)) {
                redesignSecondCircle(IndexRow, IndexCol);
            }

        }
    }

    // check if the tube is valid
    private boolean isConnectingLineValid() {
        if (mCellsUsed.size() > 0) {
            Cell cell_first = mCellsUsed.get(0);
            for (Cell item : mCellsUsed) {
                if (item.getColor() != cell_first.getColor() && item.isUsed()) {
                    return false;
                }

            }
        }

        return true;
    }

    private void setMoves() {
        TextView text_score = findViewById(R.id.moveCount);
        text_score.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_score.setText("Moves: " + String.valueOf(mMoveScore));

        TextView best_score = findViewById(R.id.bestCount);
        best_score.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        best_score.setText("Best: " + String.valueOf(mBestScore));

    }
    // update the score
    private void setScore() {
        TextView text_score = findViewById(R.id.linesCount);
        text_score.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_score.setText("Lines: " + String.valueOf(mGameScore));

    }

    // update the level
    private void setLevel(int level) {
        TextView text_level = findViewById(R.id.levelNumber);
        text_level.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_level.setText("Level: " + level + " ");
    }

    // update the size
    private void setSize(int size) {
        TextView text_size = findViewById(R.id.sizeId);
        text_size.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_size.setText(getString(R.string.boardSize, size, size));
    }

    // create the table with the right circles
    private void BuildTable(int size) {
        setLevel(level);
        setSize(size);

        clearTableLayout();
        // Fill the TableLayout
        for (int i = 0; i < size; i++) {

            TableRow row = new TableRow(mContext);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < size; col++) {
                row.addView(mTableCellsArray[i][col]);
            }

            mGameTable.addView(row);
        }

    }

    public void setMeasurements() {
        mGameTable.setMeasureWithLargestChildEnabled(true);
        mGameTableWidth = mGameTable.getMeasuredWidth();
        mGameTableHeight = mGameTable.getMeasuredHeight();

        mCellWidth = mGameTableWidth / GameActivity.this.size;
        mCellHeight = mGameTableHeight / GameActivity.this.size;
    }

    // clear the elements of the table
    private void clearTableLayout() {
        int count = mGameTable.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = mGameTable.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        mGameTable.removeAllViewsInLayout();
    }

    // Increasing the level
    private void goNextLevel() {
        if (size == 5) {
            if (level < levelCount) {
                ++level;
            } else {
                ++size;
                level = 1;
            }
        } else if (size == 6) {
            if (level < levelCount) {
                ++level;
            } else {
                ++size;
                level = 1;
            }
        } else if (size == 7) {
            if (level < levelCount) {
                ++level;
            } else {
                ++size;
                level = 1;
            }
        } else if (size == 8) {
            if (level < 3) {
                ++level;
            }
        }
        InitialiseNewTabelCellsArray(size, level);

        // build new table
        // BuildTable is used to redraw the table constantly
        mGameTable
                .animate()
                .alpha(0)
                .setDuration(500)
                .translationX(-750)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                BuildTable(size);
                mGameTable.setX(750);
                mGameTable.animate().alpha(1).translationX(0).setDuration(500);
            }
        });

        mGameScore = 0;
        mMoveScore = 0;
        setScore();
        setMoves();
    }

    // Initialize the game
    private void resetGame() {
        InitialiseNewTabelCellsArray(size, level);
        BuildTable(size);
        mGameScore = 0;
        mMoveScore = 0;
        setScore();
        setMoves();
    }

    // Initialize the table with the right circles according to the level
    // @TODO replace this with a proper generation algorithm
    // @TODO or load from a pre-generated list of levels with place keys


    // use Utilities.mockDataReader(filename) to load a JSON file into a string
    // see https://stackoverflow.com/questions/48936485/json-parser-in-android-using-local-json-file
    private void InitialiseNewTabelCellsArray(int size, int level) {

        mTableCellsArray = new Cell[size][size];

        if (size == 5) {
            GameLevel gameLevel = new GameLevel(getApplicationContext(), "Level " + String.valueOf(level), level, size);

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
            mTableCellsArray = gameLevel.getGameLayout();
        }

        if (size == 6) {
            GameLevel gameLevel = new GameLevel(getApplicationContext(), "Level " + String.valueOf(level), level, size);

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
            mTableCellsArray = gameLevel.getGameLayout();
        }


        if (size == 7) {
            GameLevel gameLevel = new GameLevel(getApplicationContext(), "Level " + String.valueOf(level), level, size);

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
            mTableCellsArray = gameLevel.getGameLayout();
        }

        if (size == 8) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {

                    // Level 1
                    if (level == 1) {
                        if ((row == 1 && col == 5) || (row == 1 && col == 7)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.RoyalBlue), row, col, true);
                        } else if ((row == 0 && col == 4) || (row == 5 && col == 4)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Red), row, col, true);
                        } else if ((row == 3 && col == 0) || (row == 3 && col == 6)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Yellow), row, col, true);
                        } else if ((row == 4 && col == 3) || (row == 5 && col == 2)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Orange), row, col, true);
                        } else if ((row == 1 && col == 0) || (row == 2 && col == 2)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.LimeGreen), row, col, true);
                        } else if ((row == 0 && col == 0) || (row == 2 && col == 0)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Maroon), row, col, true);
                        } else if ((row == 2 && col == 7) || (row == 7 && col == 7)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.PaleTurquoise), row, col, true);
                        } else if ((row == 1 && col == 6) || (row == 2 && col == 5)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Gray), row, col, true);
                        } else if ((row == 3 && col == 5) || (row == 4 && col == 2)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.PaleGreen), row, col, true);
                        } else {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, Color.TRANSPARENT, row, col, false);
                        }
                    }
                    // Level 2
                    if (level == 2) {
                        if ((row == 6 && col == 2) || (row == 5 && col == 5)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.RoyalBlue), row, col, true);
                        } else if ((row == 6 && col == 1) || (row == 4 && col == 3)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Red), row, col, true);
                        } else if ((row == 0 && col == 5) || (row == 3 && col == 5)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Yellow), row, col, true);
                        } else if ((row == 1 && col == 4) || (row == 6 && col == 3)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Orange), row, col, true);
                        } else if ((row == 1 && col == 6) || (row == 3 && col == 6)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.LimeGreen), row, col, true);
                        } else if ((row == 0 && col == 4) || (row == 0 && col == 6)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.PaleTurquoise), row, col, true);
                        } else if ((row == 2 && col == 2) || (row == 4 && col == 2)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType
                                    .None, ContextCompat.getColor(this, R.color.PaleGreen), row, col, true);
                        } else {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, Color.TRANSPARENT, row, col, false);
                        }
                    }
                    // Level 3
                    if (level == 3) {
                        if ((row == 1 && col == 1) || (row == 6 && col == 2)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.RoyalBlue), row, col, true);
                        } else if ((row == 5 && col == 2) || (row == 4 && col == 4)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Red), row, col, true);
                        } else if ((row == 1 && col == 5) || (row == 5 && col == 3)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Yellow), row, col, true);
                        } else if ((row == 1 && col == 3) || (row == 3 && col == 4)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Orange), row, col, true);
                        } else if ((row == 3 && col == 0) || (row == 0 && col == 3)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.LimeGreen), row, col, true);
                        } else if ((row == 1 && col == 2) || (row == 3 && col == 3)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.Maroon), row, col, true);
                        } else if ((row == 4 && col == 0) || (row == 1 && col == 4)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.PaleTurquoise), row, col, true);
                        } else if ((row == 2 && col == 5) || (row == 5 && col == 4)) {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, ContextCompat.getColor(this, R.color.PaleGreen), row, col, true);
                        } else {
                            mTableCellsArray[row][col] = new Cell(this, Cell.CellShape.Circle, Cell.CellType.None, Color.TRANSPARENT, row, col, false);
                        }
                    }
                }
            }

        }
    }

    private void applyTilt(float x, float y) {
//        long lastCallTime = mLastClickTime;
//        long now = System.currentTimeMillis();
//        mLastClickTime = now;
//        if (now - lastCallTime < MIN_DELAY_MS) {
//            // Too fast: ignore
//
//        } else {
//            mTableHolder.setRotationX(1 - ((x / mGameTableWidth) * 2));
//            mTableHolder.setRotationY(((y / mGameTableHeight) * 2) - 1);
//        }
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

}