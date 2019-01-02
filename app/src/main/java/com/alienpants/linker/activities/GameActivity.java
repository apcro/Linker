package com.alienpants.linker.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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

import java.util.List;
import java.util.Objects;

import static android.graphics.Color.TRANSPARENT;


public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();

    private int SCORE_DIFFERENCE = 3;
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
    int mGameScore, mMoveScore, mBestScore = 0;
    int mColourChosen;
    int mCurrentTouchedRow, mCurrentTouchedCol;
    Dialog mExitAlert;
    Dialog mWinAlert;

    Context mContext;

    private ImageView mTouchCircle;

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.hideUI(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utilities.hideUI(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Utilities.hideUI(this);

        mContext = this;

        mBackend = Linker.getBackend();
        mLevelsBox = ((Linker) getApplication()).getBoxStore().boxFor(LevelData.class);

        mTableSize = Objects.requireNonNull(this.getIntent().getExtras()).getInt("mTableSize");
        mCurrentLevel = this.getIntent().getExtras().getInt("mCurrentLevel");

        // get scores, and set them if needed
        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, mTableSize).build().find();

        mLevelCount = levels.size();

        for (int i = 0; i < mLevelCount; i++) {
            if (levels.get(i).getNum() == mCurrentLevel) {
                mThisLevel = levels.get(i);
            }
        }
        mBestScore = mThisLevel.getBestScore();

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
        Objects.requireNonNull(mWinAlert.getWindow()).getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        mWinAlert.setContentView(R.layout.dialog_success);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.snowdream);

        TextView successTextNextLevel = mWinAlert.findViewById(R.id.successTextNextLevel);
        successTextNextLevel.setTypeface(typeface);

        Button yes = mWinAlert.findViewById(R.id.btn_yes);

        yes.setOnClickListener(view -> {
            goNextLevel();
            mWinAlert.dismiss();
            Utilities.hideUI(this);
        });

        Button no = mWinAlert.findViewById(R.id.btn_no);
        no.setOnClickListener(view -> {
            resetGame();
            saveData();
            mWinAlert.dismiss();
            Utilities.hideUI(this);
        });

        Button list = mWinAlert.findViewById(R.id.btn_list);
        list.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, LevelsActivity.class);
            startActivity(intent);
            finish();
        });

        // Show message to exit the game
        mExitAlert = new Dialog(GameActivity.this);

        mExitAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(mExitAlert.getWindow()).getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        mExitAlert.setContentView(R.layout.dialog_quit);

        Button exitNo = mExitAlert.findViewById(R.id.btn_no);
        exitNo.setOnClickListener(v -> {
            mExitAlert.dismiss();
            Utilities.hideUI(getParent());
        });
        Button exitYes = mExitAlert.findViewById(R.id.btn_yes);
        exitYes.setOnClickListener(v -> finish());

        // Buttons to initialize and exit the game
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(v -> resetGame());

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(v -> {
            Objects.requireNonNull(mExitAlert.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            mExitAlert.show();
        });

        mTouchCircle = findViewById(R.id.touchCircle);
        mTouchCircle.setX(-100);
        mTouchCircle.setY(-100);
        mTouchCircle.setVisibility(View.GONE);

        updateMovesCounter();
        updateScoreCounter();

        mGameTable.setMeasureWithLargestChildEnabled(true);

        mGameTable.setOnTouchListener(new View.OnTouchListener() {

            int lastTouchedRow = 0;
            int lastTouchedCol = 0;

            boolean canDraw = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mGameTableHeight == 0) {
                    setMeasurements();
                }

                // position of the box pressed
                int touchedTableRow = (int) motionEvent.getY() / mCellWidth;
                int touchedTableCol = (int) motionEvent.getX() / mCellHeight;

                // Check that the touchedTableRow and touchedTableCol will not exceed the limits
                touchedTableRow = touchedTableRow > (mTableSize - 1) ? (mTableSize - 1) : touchedTableRow;
                touchedTableCol = touchedTableCol > (mTableSize - 1) ? (mTableSize - 1) : touchedTableCol;

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    mCurrentTouchedRow = touchedTableRow;
                    mCurrentTouchedCol = touchedTableCol;
                    lastTouchedRow = -1;
                    lastTouchedCol = -1;
                    canDraw = false;

                    // is it a circle?
                    Cell.CellShape thisCell = mTableCellsArray[touchedTableRow][touchedTableCol].getCellShape();

                    switch (thisCell) {
                        case Circle:
                            canDraw = true;

                            mColourChosen = mTableCellsArray[touchedTableRow][touchedTableCol].getColour();

                            // show touch circle
                            showTouchCircle(motionEvent.getRawX(), motionEvent.getRawY());

                            // @TODO find second circle and embiggen
                            embiggen(touchedTableRow, touchedTableCol, mColourChosen);
                            break;
                    }

                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    hideTouchCircle();

                    // only count possible moves
                    if (canDraw) {
                        mMoveScore++;
                        updateMovesCounter();
                    }

                    // are we on a circle?
                    Cell.CellShape thisCellShape = mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].getCellShape();
                    int thisCellColour = mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].getColour();

                    if (lastTouchedCol == -1 && lastTouchedRow == -1) {
                        canDraw = false;
                    }

                    if (thisCellColour == mColourChosen && thisCellShape == Cell.CellShape.Circle && canDraw) {
                        Log.d(TAG, "ACTION_UP on circle");

                        // yes, valid
                        mGameScore++;
                        updateScoreCounter();

                        Cell.CellShape lastCellShape = mTableCellsArray[lastTouchedRow][lastTouchedCol].getCellShape();
                        switch (lastCellShape) {
                            case RightHalf:
                                if (lastTouchedRow > mCurrentTouchedRow) {
                                    // right up
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightUp);
                                } else if (lastTouchedRow < mCurrentTouchedRow) {
                                    // right down
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightDown);
                                } else {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftRight);
                                }

                                break;
                            case LeftHalf:
                                if (lastTouchedRow > mCurrentTouchedRow) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftUp);
                                } else if (lastTouchedRow < mCurrentTouchedRow) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftDown);
                                } else {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftRight);
                                }
                                break;


                            case DownHalf:
                                if (lastTouchedCol > mCurrentTouchedCol) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftDown);
                                } else if (lastTouchedCol < mCurrentTouchedCol) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightDown);
                                } else {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.UpDown);
                                }
                                break;
                            case UpHalf:
                                if (lastTouchedCol < mCurrentTouchedCol) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightUp);
                                } else if (lastTouchedCol > mCurrentTouchedCol) {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftUp);
                                } else {
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.UpDown);
                                }
                                break;
                            case Circle:
                                if (lastTouchedRow > mCurrentTouchedRow) {
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                } else if (lastTouchedRow < mCurrentTouchedRow) {
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                } else if (lastTouchedCol > mCurrentTouchedCol) {
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                } else {
                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                    mTableCellsArray[mCurrentTouchedRow][mCurrentTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                }
                                break;
                            case RightUp:
                            case RightDown:
                            case UpDown:
                            case LeftDown:
                            case LeftUp:
                                // these are all error states
                                unWind(canDraw);
                                canDraw = false;
                                break;
                        }
                        redrawTable();
                    } else {
                        unWind(canDraw);
                        canDraw = false;
                    }

                    // only process if we've actually moved on to a new cell

                    // have we filled the table?
                    if (isTableComplete()) {
                        saveData();
                        Objects.requireNonNull(mWinAlert.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                        ImageView starOne = mWinAlert.findViewById(R.id.starOne);
                        ImageView starTwo = mWinAlert.findViewById(R.id.starTwo);
                        ImageView starThree = mWinAlert.findViewById(R.id.starThree);

                        if (mGameScore == mMoveScore) {
                            starOne.setImageResource(R.drawable.star_on);
                            starTwo.setImageResource(R.drawable.star_on);
                            starThree.setImageResource(R.drawable.star_on);
                        } else if (mMoveScore > mGameScore + SCORE_DIFFERENCE) {
                            starOne.setImageResource(R.drawable.star_on);
                            starTwo.setImageResource(R.drawable.star_off);
                            starThree.setImageResource(R.drawable.star_off);
                        } else {
                            starOne.setImageResource(R.drawable.star_on);
                            starTwo.setImageResource(R.drawable.star_on);
                            starThree.setImageResource(R.drawable.star_off);
                        }
                        saveData();
                        unlockNextLevel();
                        mWinAlert.show();
                    }

                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    // show touch circle
                    if (canDraw) {
                        showTouchCircle(motionEvent.getRawX(), motionEvent.getRawY());
                    }

                    if (touchedTableRow != mCurrentTouchedRow || touchedTableCol != mCurrentTouchedCol) {
                        Log.d(TAG, "ACTION_MOVE, new cell");

                        // we need to check if the move is valid, i.e. not diagonal
                        if (mCurrentTouchedRow == touchedTableRow && (mCurrentTouchedCol == touchedTableCol - 1 || mCurrentTouchedCol == touchedTableCol + 1)) {
                            Log.d(TAG, "part the one");
                            lastTouchedRow = mCurrentTouchedRow;
                            lastTouchedCol = mCurrentTouchedCol;
                            mCurrentTouchedRow = touchedTableRow;
                            mCurrentTouchedCol = touchedTableCol;
                        } else if (mCurrentTouchedCol == touchedTableCol && ((mCurrentTouchedRow == touchedTableRow - 1) || (mCurrentTouchedRow == touchedTableRow + 1))) {
                            Log.d(TAG, "part the two");
                            lastTouchedRow = mCurrentTouchedRow;
                            lastTouchedCol = mCurrentTouchedCol;
                            mCurrentTouchedRow = touchedTableRow;
                            mCurrentTouchedCol = touchedTableCol;
                        } else {
                            Log.d(TAG, "part the three");
                            canDraw = false;
                        }

                        // can't move from corner cell to new cell
                        Cell.CellShape lastCellShape = mTableCellsArray[lastTouchedRow][lastTouchedCol].getCellShape();
                        switch (lastCellShape) {
                            case Circle:
                            case DownHalf:
                            case LeftHalf:
                            case RightHalf:
                            case UpHalf:
                                break;
                            default:
                                unWind(canDraw);
                                canDraw = false;
                                break;
                        }

                        if (canDraw) {
                            Cell.CellShape thisCellShape = mTableCellsArray[touchedTableRow][touchedTableCol].getCellShape();
                            int lastCellColour = mTableCellsArray[lastTouchedRow][lastTouchedCol].getColour();
                            int thisCellColour = mTableCellsArray[touchedTableRow][touchedTableCol].getColour();

                            switch (thisCellShape) {
                                case None:
                                case Circle:
                                    if (thisCellColour == mColourChosen && thisCellShape == Cell.CellShape.Circle) {

                                        Log.d(TAG, "Last circle");

//                                        if (lastCellShape == Cell.CellShape.Circle) {
//                                            if (touchedTableRow > lastTouchedRow) {
//                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleDown);
//                                            } else if (touchedTableRow < lastTouchedRow) {
//                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleUp);
//                                            } else if (touchedTableCol > lastTouchedCol) {
//                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleRight);
//                                            } else {
//                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
//                                            }
//                                        }

                                    } else if (lastCellColour == mColourChosen && thisCellColour == TRANSPARENT) {
                                        Log.d(TAG, "Valid cell chosen");
                                        // is the last cell valid?
                                        // all good, add new entry
                                        if (lastTouchedRow < touchedTableRow) {
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setCellShape(Cell.CellShape.UpHalf);
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setColour(mColourChosen);
                                            if (lastCellShape == Cell.CellShape.Circle) {
                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                            }
                                        }
                                        if (lastTouchedRow > touchedTableRow) {
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setCellShape(Cell.CellShape.DownHalf);
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setColour(mColourChosen);
                                            if (lastCellShape == Cell.CellShape.Circle) {
                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                            }
                                        }
                                        if (lastTouchedCol < touchedTableCol) {
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setCellShape(Cell.CellShape.LeftHalf);
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setColour(mColourChosen);
                                            if (lastCellShape == Cell.CellShape.Circle) {
                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                            }
                                        }
                                        if (lastTouchedCol > touchedTableCol) {
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setCellShape(Cell.CellShape.RightHalf);
                                            mTableCellsArray[touchedTableRow][touchedTableCol].setColour(mColourChosen);
                                            if (lastCellShape == Cell.CellShape.Circle) {
                                                mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                            }
                                        }

                                        // update previous cell
                                        switch (lastCellShape) {
                                            case UpHalf:
                                            case DownHalf:
                                                if (touchedTableCol > lastTouchedCol) {
                                                    // up or down to right
                                                    if (lastCellShape == Cell.CellShape.UpHalf) {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightUp);
                                                    } else {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightDown);
                                                    }
                                                } else if (touchedTableCol < lastTouchedCol) {
                                                    // up or down to right
                                                    if (lastCellShape == Cell.CellShape.DownHalf) {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftDown);
                                                    } else {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftUp);
                                                    }
                                                } else {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.UpDown);
                                                }

                                                break;
                                            case LeftHalf:
                                            case RightHalf:
                                                if (touchedTableRow > lastTouchedRow) {
                                                    if (lastCellShape == Cell.CellShape.LeftHalf) {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftDown);
                                                    } else {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightDown);
                                                    }

                                                } else if (touchedTableRow < lastTouchedRow) {
                                                    if (lastCellShape == Cell.CellShape.LeftHalf) {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftUp);   // correct
                                                    } else {
                                                        mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.RightUp);
                                                    }
                                                } else {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.LeftRight);
                                                }
                                                break;
                                            case Circle:
                                                if (touchedTableRow > lastTouchedRow) {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleDown);
                                                } else if (touchedTableRow < lastTouchedRow) {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleUp);
                                                } else if (touchedTableCol > lastTouchedCol) {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleRight);
                                                } else {
                                                    mTableCellsArray[lastTouchedRow][lastTouchedCol].setCellShape(Cell.CellShape.CircleLeft);
                                                }
                                                break;

                                        }
                                    }
                                    break;
                            }

                            redrawTable();
                        }
                    } else {
                        // do nothing
                        Log.d(TAG, "ACTION_MOVE, do nothing");
                    }
                    return true;
                }

                return false;
            }
        });
    }

    private void unWind(boolean canDraw) {
        // unwind
        if (canDraw) {
            for (int row = 0; row < mTableSize; row++) {
                for (int col = 0; col < mTableSize; col++) {
                    if (mTableCellsArray[row][col].getColour() == mColourChosen) {
                        switch (mTableCellsArray[row][col].getCellShape()) {
                            case Circle:
                                break;
                            case CircleDown:
                            case CircleLeft:
                            case CircleRight:
                            case CircleUp:
                                mTableCellsArray[row][col].setCellShape(Cell.CellShape.Circle);
                                mTableCellsArray[row][col].setColour(mColourChosen);
                                break;
                            default:
                                mTableCellsArray[row][col].setCellShape(Cell.CellShape.Circle);
                                mTableCellsArray[row][col].setColour(TRANSPARENT);
                                break;
                        }
                    }
                }
            }
        }
        redrawTable();
    }

    private void embiggen(int row, int col, int colour) {

    }

    private void redrawTable() {
        clearTableLayout();
        // Fill the TableLayout
        for (int i = 0; i < mTableSize; i++) {

            TableRow row = new TableRow(mContext);
            row.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < mTableSize; col++) {
                row.addView(mTableCellsArray[i][col]);
            }
            mGameTable.addView(row);
        }
    }

    private void showTouchCircle(float x, float y) {
        // show touch circle
        mTouchCircle.setX(x - 96 - 24);
        mTouchCircle.setY(y - 96 - 24);
        ImageViewCompat.setImageTintList(mTouchCircle, ColorStateList.valueOf(mColourChosen));
        mTouchCircle.setVisibility(View.VISIBLE);
    }

    private void hideTouchCircle() {
        mTouchCircle.setX(-100);
        mTouchCircle.setY(-100);
        mTouchCircle.setVisibility(View.GONE);
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

    private boolean isTableComplete() {
        for (int row = 0; row < mTableSize; ++row) {
            for (int col = 0; col < mTableSize; ++col) {
                // any empty slots, not finished
                if (mTableCellsArray[row][col].getColour() == TRANSPARENT) {
                    return false;
                }
                // any unconnected starts/ends next to each other
                if (mTableCellsArray[row][col].getCellShape() == Cell.CellShape.Circle) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMovesCounter() {
        TextView moveCount = findViewById(R.id.moveCount);
        moveCount.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        moveCount.setText(getString(R.string.moveCount, mMoveScore));

        TextView bestCount = findViewById(R.id.bestCount);
        bestCount.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        bestCount.setText(getString(R.string.bestCount, mBestScore));

    }
    // update the score
    private void updateScoreCounter() {
        TextView linesCount = findViewById(R.id.linesCount);
        linesCount.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        linesCount.setText(getString(R.string.linesCount, mGameScore));

    }

    // update the mCurrentLevel
    private void setLevel(int level) {
        TextView levelNumber = findViewById(R.id.levelNumber);
        levelNumber.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        levelNumber.setText(getString(R.string.levelNumber, level));
    }

    private void updateTableSizeText(int size) {
        TextView sizeId = findViewById(R.id.sizeId);
        sizeId.setTypeface(ResourcesCompat.getFont(this, R.font.hvdcomicserifpro), Typeface.NORMAL);
        sizeId.setText(getString(R.string.boardSize, size, size));
    }

    // create the table with the right circles
    private void BuildTable(int size) {
        setLevel(mCurrentLevel);
        updateTableSizeText(size);

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
        for (int i = 0; i < mTableSize; i++) {
            View child = mGameTable.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        mGameTable.removeAllViewsInLayout();
    }

    private void unlockNextLevel() {
        int currLevel = mCurrentLevel;
        int currCount = mLevelCount;
        int currTableSize = mTableSize;

        if (currLevel < currCount) {
            currLevel++;
        } else {
            currTableSize++;
            currLevel = 1;
        }

        // update mCurrentLevel locks
        List<LevelData> levels = mLevelsBox.query().equal(LevelData_.size, currTableSize).and().equal(LevelData_.num, currLevel).build().find();
        LevelData thislevel = levels.get(0);
        thislevel.unlock();
        mLevelsBox.put(thislevel);
    }

    // Increasing the mCurrentLevel
    private void goNextLevel() {

        unlockNextLevel();

        if (mCurrentLevel < mLevelCount) {
            mCurrentLevel++;
        } else {
            mTableSize++;
            mCurrentLevel = 1;
        }

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
        updateScoreCounter();
        updateMovesCounter();
    }

    // Initialize the game
    private void resetGame() {
        InitialiseNewTableCellsArray(mTableSize, mCurrentLevel);
        BuildTable(mTableSize);
        mGameScore = 0;
        mMoveScore = 0;
        updateScoreCounter();
        updateMovesCounter();
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