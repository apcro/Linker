package com.alienpants.linker.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.alienpants.linker.R;

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
    private Paint blurPaint;

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
        this.blurPaint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);

//        this.blurPaint.setAntiAlias(true);
//        this.blurPaint.setDither(true);
//        this.blurPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
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
        this.blurPaint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);

//        this.blurPaint.setAntiAlias(true);
//        this.blurPaint.setDither(true);
//        this.blurPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
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
        this.blurPaint = new Paint();

        this.paint.setAntiAlias(true);
        this.paint.setDither(true);

//        this.blurPaint.setAntiAlias(true);
//        this.blurPaint.setDither(true);
//        this.blurPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(this.color);
        blurPaint.setColor(this.color);
        this.setBackgroundResource(R.drawable.cell_shape);
//        this.setBackgroundResource(R.drawable.cell_bg);

        // the parameters of the rectangle
        float rectLeft;
        float rectTop;
        float rectRight;
        float rectBottom;

        // The parameters of the circle
        float circleCX;
        float circleCY;
        float circleRadius;

        switch (this.cellShape) {
            // Draw a circle
            case Circle:
                // Initialize the circle settings
                circleCX = (float) getWidth() / 2;
                circleCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleCX, circleCY, circleRadius, paint);
                break;

            // Draw a rectangle from top to bottom
            case UpDown:
                // Initialize the parameters of the rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = 0;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight();

                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                break;

            // Draw a rectangle from left to right
            case LeftRight:
                // Initialize the parameters of the rectangle
                rectLeft = 0;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth();
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            // Draw a rectangle half the cell, from the left
            case LeftHalf:
                rectLeft = 0;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth() / 2;
                rectBottom = (float) getHeight() * 3 / 4;
                RectF rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                canvas.drawRoundRect(rect, 2, 2, paint);
                break;

            case LeftDown:
                // Initialize the parameters of the 1st rectangle
                rectLeft = 0;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth() / 2;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);


                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = (float) getHeight() / 4;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight();
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case DownHalf:
                rectLeft = (float) getWidth() / 4;
                rectTop = (float) getHeight() / 4;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight();
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case LeftUp:
                // Initialize the parameters of the 1st rectangle
                rectLeft = 0;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth() / 2;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = 0;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case UpHalf:
                rectLeft = (float) getWidth() / 4;
                rectTop = 0;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case RightDown:
                // Initialize the parameters of the 1st rectangle
                rectLeft = (float) getWidth() / 2;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth();
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = (float) getHeight() / 4;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight();
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case RightUp:
                // Initialize the parameters of the 1st rectangle
                rectLeft = (float) getWidth() / 2;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth();
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = 0;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case RightHalf:
                rectLeft = (float) getWidth() / 2;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth();
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            // Draw a rectangle for corner from left to top
            case CircleRight:
                // Initialize the circle settings
                circleCX = (float) getWidth() / 2;
                circleCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleCX, circleCY, circleRadius, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 2;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth();
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleUp:
                // Initialize the circle settings
                circleCX = (float) getWidth() / 2;
                circleCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleCX, circleCY, circleRadius, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = 0;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight() / 2;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleLeft:
                // Initialize the circle settings
                circleCX = (float) getWidth() / 2;
                circleCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleCX, circleCY, circleRadius, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = 0;
                rectTop = (float) getWidth() / 4;
                rectRight = (float) getWidth() / 2;
                rectBottom = (float) getHeight() * 3 / 4;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleDown:
                // Initialize the circle settings
                circleCX = (float) getWidth() / 2;
                circleCY = (float) getHeight() / 2;
                circleRadius = (float) getHeight() / 3;
                canvas.drawCircle(circleCX, circleCY, circleRadius, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = (float) getWidth() / 4;
                rectTop = (float) getHeight() / 2;
                rectRight = (float) getWidth() * 3 / 4;
                rectBottom = (float) getHeight();
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                break;

            default:
                paint.setColor(Color.TRANSPARENT);
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

