package com.alienpants.linker.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.alienpants.linker.R;

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

    // Attributes of the cell
    private CellShape cellShape;
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
    public Cell(Context context, CellShape cellShape, int color, int indexRow, int indexCol, boolean used) {
        super(context);
        this.cellShape = cellShape;
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
        RectF rect;

        // The parameters of the circle
        float circleCX;
        float circleCY;
        float circleRadius;

        // let's make some reusable sizes
        float halfWidth = getWidth() / 2;
        float quarterWidth = getWidth() / 4;


        switch (this.cellShape) {
            // Draw a circle
            case Circle:
                // Initialize the circle settings
                circleCX = halfWidth;
                circleCY = halfWidth;
                canvas.drawCircle(circleCX, circleCY, (float) getHeight() / 3, paint);
                break;

            // Draw a rectangle from top to bottom
            case UpDown:
                // Initialize the parameters of the rectangle
                rectLeft = quarterWidth;
                rectTop = 0;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + halfWidth;

                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                break;

            // Draw a rectangle from left to right
            case LeftRight:
                // Initialize the parameters of the rectangle
                rectLeft = 0;
                rectTop = quarterWidth;
                rectRight = halfWidth + halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            // Draw a rectangle half the cell, from the left
            case LeftHalf:
                rectLeft = 0;
                rectTop = quarterWidth;
                rectRight = halfWidth;
                rectBottom = halfWidth + quarterWidth;
                rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                canvas.drawRoundRect(rect, quarterWidth, quarterWidth, paint);
                canvas.drawRect(rectLeft, rectTop, rectRight-quarterWidth, rectBottom, paint);
                break;

            case LeftDown:
                // Initialize the parameters of the 1st rectangle
                rectLeft = 0;
                rectTop = quarterWidth;
                rectRight = halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);


                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = halfWidth;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                canvas.drawCircle(halfWidth, halfWidth, quarterWidth, paint);
                break;

            case DownHalf:
                rectLeft = quarterWidth;
                rectTop = quarterWidth;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + halfWidth;
                rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                canvas.drawRoundRect(rect, quarterWidth, quarterWidth, paint);
                canvas.drawRect(rectLeft, rectTop+quarterWidth, rectRight, rectBottom, paint);
                break;

            case LeftUp:
                // Initialize the parameters of the 1st rectangle
                rectLeft = 0;
                rectTop = quarterWidth;
                rectRight = halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = 0;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                canvas.drawCircle(halfWidth, halfWidth, quarterWidth, paint);
                break;

            case UpHalf:
                rectLeft = quarterWidth;
                rectTop = 0;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + quarterWidth;
                rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                canvas.drawRoundRect(rect, quarterWidth, quarterWidth, paint);
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom-quarterWidth, paint);
                break;

            case RightDown:
                // Initialize the parameters of the 1st rectangle
                rectLeft = halfWidth;
                rectTop = quarterWidth;
                rectRight = halfWidth + halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = halfWidth;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                canvas.drawCircle(halfWidth, halfWidth, quarterWidth, paint);
                break;

            case RightUp:
                // Initialize the parameters of the 1st rectangle
                rectLeft = halfWidth;
                rectTop = quarterWidth;
                rectRight = halfWidth + halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = 0;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                canvas.drawCircle(halfWidth, halfWidth, quarterWidth, paint);
                break;

            case RightHalf:
                rectLeft = halfWidth;
                rectTop = quarterWidth;
                rectRight = halfWidth + halfWidth;
                rectBottom = halfWidth + quarterWidth;
                rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                canvas.drawRoundRect(rect, quarterWidth, quarterWidth, paint);
                canvas.drawRect(rectLeft+quarterWidth, rectTop, rectRight, rectBottom, paint);
                break;

            // Draw a rectangle for corner from left to top
            case CircleRight:
                // Initialize the circle settings
                circleCX = halfWidth;
                circleCY = halfWidth;
                canvas.drawCircle(circleCX, circleCY, (float) getHeight() / 3, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = halfWidth;
                rectTop = quarterWidth;
                rectRight = halfWidth + halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleUp:
                // Initialize the circle settings
                circleCX = halfWidth;
                circleCY = halfWidth;
                canvas.drawCircle(circleCX, circleCY, (float) getHeight() / 3, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = 0;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleLeft:
                // Initialize the circle settings
                circleCX = halfWidth;
                circleCY = halfWidth;
                canvas.drawCircle(circleCX, circleCY, (float) getHeight() / 3, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = 0;
                rectTop = quarterWidth;
                rectRight = halfWidth;
                rectBottom = halfWidth + quarterWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
                break;

            case  CircleDown:
                // Initialize the circle settings
                circleCX = halfWidth;
                circleCY = halfWidth;
                canvas.drawCircle(circleCX, circleCY, (float) getHeight() / 3, paint);

                // Initialize the parameters of the 2nd rectangle
                rectLeft = quarterWidth;
                rectTop = halfWidth;
                rectRight = halfWidth + quarterWidth;
                rectBottom = halfWidth + halfWidth;
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);

                break;

            default:
                paint.setColor(Color.TRANSPARENT);
                canvas.drawCircle(halfWidth, halfWidth, getHeight() / 3, paint);
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

    public int getColour() {
        return color;
    }

    public void setColour(int color) {
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

