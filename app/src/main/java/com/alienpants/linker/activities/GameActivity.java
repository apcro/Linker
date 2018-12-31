package com.alienpants.linker.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;
import io.objectbox.Box;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;


import com.alienpants.linker.application.Linker;
import com.alienpants.linker.data.GameLevel;
import com.alienpants.linker.data.LevelData;
import com.alienpants.linker.data.LevelData_;
import com.alienpants.linker.libraries.Backend;
import com.alienpants.linker.libraries.Utilities;
import com.alienpants.linker.models.Cell;
import com.alienpants.linker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();

    LinearLayout mTableHolder;
    TableLayout mGameTable;
    int mGameTableWidth, mGameTableHeight;
    int mCellWidth, mCellHeight;

    Backend mBackend;
    Box mLevelsBox;

    LevelData mThisLevel;

    int mTableSize = 7;
    int mCurrentLevel = 1;
    int mLevelCount;

    Cell[][] mTableCellsArray;
    ArrayList<Cell> mCellsUsed = new ArrayList<>();
    int mGameScore, mMoveScore, mBestScore = 0;
    boolean active_draw = false;
    int mColourChosen;
    int IndexPreviousCellRow = 0;
    int IndexPreviousCellCol = 0;
    int IndexCurrentCellRow = 0;
    int IndexCurrentCellCol = 0;
    int mCurrentTouchedRow, mCurrentTouchedCol;
    Cell mPreviousCell;
    Cell mCurrentCell;
    private boolean over = false;
    AlertDialog.Builder mExitAlert;
    AlertDialog.Builder mGameOverAlert;
    Dialog mWinAlert;

    Context mContext;

    private ImageView mTouchCircle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Utilities.hideUI(this);

        mContext = this;

        mBackend = Linker.getBackend();
        mLevelsBox = ((Linker) getApplication()).getBoxStore().boxFor(LevelData.class);

        mTableSize = this.getIntent().getExtras().getInt("mTableSize");
        mCurrentLevel = this.getIntent().getExtras().getInt("mCurrentLevel");

        // get scores, and set them if needed
        List<LevelData> levels = mLevelsBox.query()
                .equal(LevelData_.size, mTableSize)
                .and()
                .equal(LevelData_.num, mCurrentLevel)
                .build().find();

        mThisLevel = levels.get(0);
        mBestScore = mThisLevel.getBestScore();
        mLevelCount = levels.size();

        mGameTable = findViewById(R.id.table_game);
        mTableHolder = findViewById(R.id.tableHolder);

        mGameTableWidth = 0;
        mGameTableHeight = 0;
        mCellWidth = 0;
        mCellHeight = 0;

        // Fill the table according to the mCurrentLevel and the mTableSize passed in parameters
        InitialiseNewTableCellsArray(mTableSize, mCurrentLevel);
        BuildTable(mTableSize);

        // Show victory message
        mWinAlert = new Dialog(GameActivity.this);
        mWinAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mWinAlert.setContentView(R.layout.dialog_success);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.snowdream);

        TextView successTextNextLevel = mWinAlert.findViewById(R.id.successTextNextLevel);
        successTextNextLevel.setTypeface(typeface);

        Button yes = mWinAlert.findViewById(R.id.btn_yes);
        yes.setTypeface(typeface);

        yes.setOnClickListener(view -> {
            saveData();
            goNextLevel();
            mWinAlert.dismiss();
            Utilities.hideUI(this);
        });

        Button no = mWinAlert.findViewById(R.id.btn_no);
        no.setTypeface(typeface);
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
        mExitAlert.setPositiveButton("Yes", (dialog, id) -> finish());
        mExitAlert.setNegativeButton("Cancel", (dialog, id) -> {});

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

        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(v -> mExitAlert.show());

        mTouchCircle = findViewById(R.id.touchCircle);
        mTouchCircle.setX(-100);
        mTouchCircle.setY(-100);
        mTouchCircle.setVisibility(View.GONE);

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
            IndexRow = IndexRow > (mTableSize - 1) ? (mTableSize - 1) : IndexRow;
            IndexCol = IndexCol > (mTableSize - 1) ? (mTableSize - 1) : IndexCol;

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
                        mPreviousCell = mCellsUsed.get(mCellsUsed.size() - 1);
                        mCurrentCell = mCellsUsed.get(mCellsUsed.size() - 1);
                    }
                }

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {


                // hide touch circle
                mTouchCircle.setX(-100);
                mTouchCircle.setY(-100);
                mTouchCircle.setVisibility(View.GONE);

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
                    if (mCurrentLevel == 3 && mTableSize == 8) {
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

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {

                // show touch circle
                mTouchCircle.setX(event.getRawX() - 96 - 24);
                mTouchCircle.setY(event.getRawY() - 96 - 24);
                ImageViewCompat.setImageTintList(mTouchCircle, ColorStateList.valueOf(mColourChosen));
                mTouchCircle.setVisibility(View.VISIBLE);

                if (mCellsUsed.size() > 0 && (mTableCellsArray[IndexRow][IndexCol].getColor() == Color.TRANSPARENT || mTableCellsArray[IndexRow][IndexCol].isUsed())) {
                    if (!addCellUsed(mTableCellsArray[IndexRow][IndexCol], false)) {
                        active_draw = false;
                    }
                }

                if (mCellsUsed.size() > 1) {
                    mPreviousCell = mCellsUsed.get(mCellsUsed.size() - 2);
                    mCurrentCell = mCellsUsed.get(mCellsUsed.size() - 1);
                    IndexPreviousCellRow = mPreviousCell.getIndexRow();
                    IndexPreviousCellCol = mPreviousCell.getIndexCol();
                    IndexCurrentCellRow = mCurrentCell.getIndexRow();
                    IndexCurrentCellCol = mCurrentCell.getIndexCol();
                }

                // Draw lines between circles
                DrawLine();

                return true;
            }
            return false;
        });

    }

    private void saveData() {
        int best = mThisLevel.getBestScore();
        if (mMoveScore == mGameScore) {
            mThisLevel.setScore(3);
        } else if (mMoveScore < (mGameScore + 3)) {
            mThisLevel.setScore(2);
        } else {
            mThisLevel.setScore(1);
        }
        if (mMoveScore < best) {
            mThisLevel.setBestScore(mMoveScore);
        }
        mThisLevel.setLocked(false);
        mLevelsBox.put(mThisLevel);

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

        for (int row = 0; row < mTableSize; ++row) {
            for (int col = 0; col < mTableSize; ++col) {
                // any empty slots, not finished
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
        int numberFirstCircle = 0;
        int numberSecondCircle = 0;
        int numberUsedCircle = 0;
        for (int row = 0; row < mTableSize; ++row) {
            for (int col = 0; col < mTableSize; ++col) {
                if (mTableCellsArray[row][col].isUsed()) {
                    numberUsedCircle++;
                    if (mTableCellsArray[row][col].getType() == Cell.CellType.First) {
                        numberFirstCircle++;
                    } else if (mTableCellsArray[row][col].getType() == Cell.CellType.Second) {
                        numberSecondCircle++;
                    }
                }
            }
        }

        if ((numberFirstCircle == numberSecondCircle) && (numberFirstCircle == numberUsedCircle / 2)) {
            game_over = true;
        }
        return game_over && !isTableComplete();
    }

    // Function that checks if two circles of the same colors are connected
    private boolean isConnected(Cell cell) {
        boolean connected = false;
        if (cell.getType() != Cell.CellType.None && cell.isUsed()) {
            for (int row = 0; row < mTableSize; ++row) {
                for (int col = 0; col < mTableSize; ++col) {
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
        for (int row = 0; row < mTableSize; ++row) {
            for (int col = 0; col < mTableSize; ++col) {
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
        BuildTable(mTableSize);
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
                    BuildTable(mTableSize);
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
                    BuildTable(mTableSize);
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
                    BuildTable(mTableSize);
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
                    BuildTable(mTableSize);
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
                Log.d(TAG, "1");
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.DownHalf);
            } else

            // From Up to Down
            if (IndexPreviousCellRow < IndexCurrentCellRow && IndexPreviousCellCol == IndexCurrentCellCol) {
                Log.d(TAG, "2");
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.UpHalf);
            } else

            // From Right to Left
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol > IndexCurrentCellCol) {
                Log.d(TAG, "3");
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.RightHalf);
            } else

            // From Left to Right
            if (IndexPreviousCellRow == IndexCurrentCellRow && IndexPreviousCellCol < IndexCurrentCellCol) {
                Log.d(TAG, "4");
                _drawLine(IndexPreviousCellRow, IndexPreviousCellCol, mColourChosen, active_draw, Cell.CellShape.LeftHalf);
            } else {
                Log.d(TAG, "No conditions met");
                Log.d(TAG, IndexPreviousCellRow + ":" + IndexCurrentCellRow + ":" + IndexPreviousCellCol + ":" + IndexCurrentCellCol + ":" + active_draw);
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
        BuildTable(mTableSize);
    }

    // trace the lines
    private void _drawLine(int IndexRow, int IndexCol, int color_chosen, boolean active, Cell.CellShape cellShape) {
        if (active_draw) {
            if (!mTableCellsArray[IndexRow][IndexCol].isUsed()) {
                if (cellShape == Cell.CellShape.UpDown && (IndexRow != 0 && IndexRow != mTableSize - 1)) {
                    mTableCellsArray[IndexRow][IndexCol].setCellShape(cellShape);
                    mTableCellsArray[IndexRow][IndexCol].setColor(color_chosen);
                    BuildTable(mTableSize);
                } else if (cellShape == Cell.CellShape.LeftRight && (IndexCol != 0 && IndexCol != mTableSize - 1)) {

                    mTableCellsArray[IndexRow][IndexCol].setCellShape(cellShape);
                    mTableCellsArray[IndexRow][IndexCol].setColor(color_chosen);
                    BuildTable(mTableSize);
//                } else if (cellShape == Cell.CellShape.Circle && (IndexCol != 0 && IndexCol != mTableSize - 1)) {
//                    mTableCellsArray[IndexRow][IndexCol].setCellShape(cellShape);
//                    mTableCellsArray[IndexRow][IndexCol].setColor(color_chosen);
//                    BuildTable(mTableSize);
//                    Log.d(TAG, "draw here, but need proper constraints");
                }
            } else if (isSecondCircle(IndexRow, IndexCol)) {
                redesignSecondCircle(IndexRow, IndexCol);
            } else {
                Log.d(TAG, "draw here?");
            }

        } else {
            Log.d(TAG, "not active_draw");
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

    // update the mCurrentLevel
    private void setLevel(int level) {
        TextView text_level = findViewById(R.id.levelNumber);
        text_level.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_level.setText("Level: " + level + " ");
    }

    // update the mTableSize
    private void setSize(int size) {
        TextView text_size = findViewById(R.id.sizeId);
        text_size.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        text_size.setText(getString(R.string.boardSize, size, size));
    }

    // create the table with the right circles
    private void BuildTable(int size) {
        setLevel(mCurrentLevel);
        setSize(size);

        clearTableLayout();
        // Fill the TableLayout
        for (int i = 0; i < size; i++) {

            TableRow row = new TableRow(mContext);
            row.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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

        mCellWidth = mGameTableWidth / GameActivity.this.mTableSize;
        mCellHeight = mGameTableHeight / GameActivity.this.mTableSize;
    }

    // clear the elements of the table
    private void clearTableLayout() {
        int count = mGameTable.getChildCount();
        for (int i = 0; i < mTableSize; i++) {
            View child = mGameTable.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        mGameTable.removeAllViewsInLayout();
    }

    // Increasing the mCurrentLevel
    private void goNextLevel() {
//        if (mTableSize == 5) {
            if (mCurrentLevel < mLevelCount) {
                mCurrentLevel++;
            } else {
                mTableSize++;
                mCurrentLevel = 1;
            }
//        } else if (mTableSize == 6) {
//            if (mCurrentLevel < mLevelCount) {
//                ++mCurrentLevel;
//            } else {
//                ++mTableSize;
//                mCurrentLevel = 1;
//            }
//        } else if (mTableSize == 7) {
//            if (mCurrentLevel < mLevelCount) {
//                ++mCurrentLevel;
//            } else {
//                ++mTableSize;
//                mCurrentLevel = 1;
//            }
//        } else if (mTableSize == 8) {
//            if (mCurrentLevel < 3) {
//                ++mCurrentLevel;
//            }
//        }
        // update mCurrentLevel locks
        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, mTableSize).and().equal(LevelData_.num, mCurrentLevel).build().find();
        LevelData thislevel = levels.get(0);
        thislevel.unlock();
        mLevelsBox.put(thislevel);

        InitialiseNewTableCellsArray(mTableSize, mCurrentLevel);

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
                        BuildTable(mTableSize);
                        setMeasurements();
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
        InitialiseNewTableCellsArray(mTableSize, mCurrentLevel);
        BuildTable(mTableSize);
        mGameScore = 0;
        mMoveScore = 0;
        setScore();
        setMoves();
    }

    // Initialize the table with the right circles according to the mCurrentLevel
    private void InitialiseNewTableCellsArray(int size, int levelNumber) {
        mTableCellsArray = new Cell[size][size];
        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, size).and().equal(LevelData_.num, levelNumber).build().find();
        LevelData level = levels.get(0);
        GameLevel gameLevel = new GameLevel(getApplicationContext(), "Level " + String.valueOf(level.getNum()), levelNumber, size);
        gameLevel.makeLayout(level.getLayout());
        mTableCellsArray = gameLevel.getGameLayout();
    }

}