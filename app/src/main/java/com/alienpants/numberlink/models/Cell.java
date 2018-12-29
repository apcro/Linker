package com.alienpants.numberlink.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.alienpants.numberlink.R;

/**
 * Created by Najib on 08/02/2016.
 */
public class Cell extends View {

    // Shape of the cell, Circle for the points to be connected
    // and Rectangle for the lines that connect them
    public enum CellShape {
        Circle,
        UpDown,
        LeftRight,
        LeftDown,
        LeftUp,
        RightDown,
        RightUp,
        CircleUp,
        CircleDown,
        CircleLeft,
        CircleRight,
        LeftHalf,
        DownHalf,
        UpHalf,
        RightHalf,
        None
    }

    // Type of cell, First: first clicked
    public enum CellType {
        First,
        Second,
        None
    }


    // Attributes of the cell
    private CellShape cellShape;
    private CellType type;
    private int color;
    private int indexRow;
    private int indexCol;
    private boolean used;
    private Paint paint;

    // Default constructor
    public Cell(Context context) {
        super(context);
        this.cellShape = CellShape.Circle;
        this.type = CellType.None;
        this.color = Color.BLACK;
        this.indexRow = 0;
        this.indexCol = 0;
        this.used = false;
        this.paint = new Paint();
    }


    // Constructor by Parameters
    public Cell(Context context, CellShape cellShape, CellType type, int color, int indexRow, int indexCol, boolean used) {
        super(context);
        this.cellShape = cellShape;
        this.type = type;
        this.color = color;
        this.indexRow = indexRow;
        this.indexCol = indexCol;
        this.used = used;
        this.paint = new Paint();
    }

    // Default constructor
    public Cell(Context context, Cell cell) {
        super(context);
        this.cellShape = cell.cellShape;
        this.type = cell.getType();
        this.color = cell.color;
        this.indexRow = cell.indexRow;
        this.indexCol = cell.indexCol;
        this.used = false;
        this.paint = new Paint();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(this.color);
        this.setBackgroundResource(R.drawable.cell_shape);

        // the parameters of the rectangle
        float rectIndexLeft;
        float rectIndexTop;
        float rectIndexRight;
        float rectIndexBottom;

        // The parameters of the circle
        float circleIndexCX;
        float circleIndexCY;
        float circleRadius;

        switch (this.cellShape) {
            // Draw a circle
            case Circle:
                // Initialize the circle settings
                circleIndexCX = (float) getWidth() / 2;
                circleIndexCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleIndexCX, circleIndexCY, circleRadius, paint);
                break;

            // Draw a rectangle from top to bottom
            case UpDown:
                // Initialize the parameters of the rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = 0;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight();
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;

            // Draw a rectangle from left to right
            case LeftRight:
                // Initialize the parameters of the rectangle
                rectIndexLeft = 0;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth();
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;

            // Draw a rectangle half the cell, from the left
            case LeftHalf:
                rectIndexLeft = 0;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth() / 2;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                RectF rect = new RectF(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom);
                canvas.drawRoundRect(rect, 2, 2, paint);
//                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;

            // Dessiner un rectangle pour corner de gauche en bas
            case LeftDown:
                // Initialize the parameters of the 1st rectangle
                rectIndexLeft = 0;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth() / 2;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = (float) getHeight() / 4;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight();
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;
                // Dessiner un rectangle pour corner de gauche en haut

            case DownHalf:
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = (float) getHeight() / 4;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight();
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;

            case LeftUp:
                // Initialize the parameters of the 1st rectangle
                rectIndexLeft = 0;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth() / 2;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = 0;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            case UpHalf:
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = 0;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;


            // Dessiner un rectangle pour corner de gauche en haut
            case RightDown:
                // Initialize the parameters of the 1st rectangle
                rectIndexLeft = (float) getWidth() / 2;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth();
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = (float) getHeight() / 4;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight();
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            // Dessiner un rectangle pour corner de gauche en haut
            case RightUp:
                // Initialize the parameters of the 1st rectangle
                rectIndexLeft = (float) getWidth() / 2;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth();
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = 0;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            case RightHalf:
                rectIndexLeft = (float) getWidth() / 2;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth();
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);
                break;

            // Draw a rectangle for corner from left to top
            case CircleRight:
                // Initialize the circle settings
                circleIndexCX = (float) getWidth() / 2;
                circleIndexCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleIndexCX, circleIndexCY, circleRadius, paint);

                // Initialiser les paramètres du 2eme rectangle
                rectIndexLeft = (float) getWidth() / 2;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth();
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            // Dessiner un rectangle pour corner de gauche en haut
            case  CircleUp:
                // Initialize the circle settings
                circleIndexCX = (float) getWidth() / 2;
                circleIndexCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleIndexCX, circleIndexCY, circleRadius, paint);

                // Initialiser les paramètres du 2eme rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = 0;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            // Dessiner un rectangle pour corner de gauche en haut
            case  CircleLeft:
                // Initialiser les paramètres du cercle
                circleIndexCX = (float) getWidth() / 2;
                circleIndexCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleIndexCX, circleIndexCY, circleRadius, paint);

                // Initialiser les paramètres du 2eme rectangle
                rectIndexLeft = 0;
                rectIndexTop = (float) getWidth() / 4;
                rectIndexRight = (float) getWidth() / 2;
                rectIndexBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            // Dessiner un rectangle pour corner de gauche en haut
            case  CircleDown:
                // Initialiser les paramètres du cercle
                circleIndexCX = (float) getWidth() / 2;
                circleIndexCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleIndexCX, circleIndexCY, circleRadius, paint);

                // Initialiser les paramètres du 2eme rectangle
                rectIndexLeft = (float) getWidth() / 4;
                rectIndexTop = (float) getHeight() / 4;
                rectIndexRight = (float) getWidth() * 3 / 4;
                rectIndexBottom = (float) getHeight();
                canvas.drawRect(rectIndexLeft, rectIndexTop, rectIndexRight, rectIndexBottom, paint);

                break;

            default:
                paint.setColor(Color.BLACK);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 3, paint);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //Sets the cells as square
        if (width > height) {
            size = height;
        } else {
            size = width;
        }
        setMeasuredDimension(size, size);
    }

    // Getters and Setters
    public CellShape getCellShape() {
        return cellShape;
    }

    public void setCellShape(CellShape cellShape) {
        this.cellShape = cellShape;
    }


    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIndexRow() {
        return indexRow;
    }

    public void setIndexRow(int indexRow) {
        this.indexRow = indexRow;
    }

    public int getIndexCol() {
        return indexCol;
    }

    public void setIndexY(int indexCol) {
        this.indexCol = indexCol;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }



}

