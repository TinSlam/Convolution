package com.tinslam.convolution.UI.Chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tinslam.convolution.Base.Game;
import com.tinslam.convolution.Concepts.Signal.ContinuousSignal;

public class Chart{
    public static final byte READ_ONLY = 0,
    REC_INPUT = 1,
    DRAG_SIGNAL = 2;

    private float x, y, width, height;
    private float realHeight;
    private Paint backgroundPaint, textPaint, scalesPaint;
    private String title;
    private float textSize = 24 * Game.density();
    private float xStart = -2.8f, xEnd = 7.2f, yStart = -3.3f, yEnd = 6.6f; // Test values.

    public Chart(String title){
        this.title = title;
        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(10);
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        scalesPaint = new Paint();
        scalesPaint.setTextSize(16 * Game.density());
        scalesPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas){
        draw(canvas, true);
    }

    public void draw(Canvas canvas, boolean verticalScale){
        canvas.drawText(title, getCenterX(), getY2() + 2 * textSize, textPaint);
        canvas.drawLine(getX(), getYAxisPosition(), getX2(), getYAxisPosition(), backgroundPaint);
        canvas.drawLine(getXAxisPosition(), getY(), getXAxisPosition(), getY2(), backgroundPaint);

        int counter = 0;
        int drawGapCounter = 0;
        int drawGap = ((int) Math.floor(xEnd) - (int) Math.ceil(xStart)) / 6 + 1;
        float oneGap = width / (xEnd - xStart);
        float offset = (float) ((Math.ceil(xStart) - xStart) * oneGap);
        for(int i = (int) Math.ceil(xStart); i <= (int) Math.floor(xEnd); i++){
            if(i == 0){
                counter++;
                continue;
            }
            if(drawGapCounter == 0){
                canvas.drawLine(getX() + offset + oneGap * counter, getYAxisPosition() - 4 * Game.density(),
                        getX() + offset + oneGap * counter, getYAxisPosition() + 4 * Game.density(),
                        backgroundPaint);
                canvas.drawText("" + i,getX() + offset + oneGap * counter, getYAxisPosition()  + 20 * Game.density(), scalesPaint);
            }
            drawGapCounter++;
            if(drawGap == drawGapCounter) drawGapCounter = 0;
            counter++;
        }
        drawGapCounter = 0;
        drawGap = ((int) Math.floor(yEnd) - (int) Math.ceil(yStart)) / 6 + 1;
        counter = 0;
        oneGap = height / (yEnd - yStart);
        offset = (float) ((Math.ceil(yStart) - yStart) * oneGap);
        for(int i = (int) Math.ceil(yStart); i <= (int) Math.floor(yEnd); i++){
            if(i == 0){
                counter++;
                continue;
            }
            if(verticalScale){
                if(drawGapCounter == 0){
                    canvas.drawLine(getXAxisPosition() - 4 * Game.density(), getY2() - offset - oneGap * counter,
                            getXAxisPosition() + 4 * Game.density(), getY2() - offset - oneGap * counter,
                            backgroundPaint);
                    canvas.drawText("" + i, getXAxisPosition() - 20 * Game.density(), getY2() - offset - oneGap * counter + 4 * Game.density(), scalesPaint);
                }
                drawGapCounter++;
                if(drawGap == drawGapCounter) drawGapCounter = 0;
                counter++;
            }
        }
    }

    public void setPosition(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.realHeight = height;
        this.height = (int) (realHeight - (textSize + 12 * Game.density()));
    }

    public void drawPoint(int x, float y, Paint paint, Canvas canvas){
        if(y < yStart || y > yEnd) return;
        canvas.drawPoint(width * x / ContinuousSignal.MAX_ENTRIES + getX(),
                getY2() - (y - yStart) / (yEnd - yStart) * height,
                paint);
    }

    public void drawDiscretePoint(int x, float y, Paint paint, Canvas canvas){
        canvas.drawCircle(width * (x - xStart) / (xEnd - xStart) + getX(),
                getY2() - (y - yStart) / (yEnd - yStart) * height,
                Game.density() * 4,
                paint);
        canvas.drawLine(width * (x - xStart) / (xEnd - xStart) + getX(),
                getYAxisPosition(),
                width * (x - xStart) / (xEnd - xStart) + getX(),
                getY2() - (y - yStart) / (yEnd - yStart) * height,
                paint);
    }

    public void addPoint(ContinuousSignal signal, float x, float y){
        y -= this.y;
        x -= this.x;
        float scale = height / (yEnd - yStart);
        y /= scale;
        scale = width / (xEnd - xStart);
        x /= scale;
        y -= yEnd;
        y = -y;
        x += xStart;
        if(Float.isNaN(y) || Float.isNaN(x)) return;
        signal.addPoint((int) (ContinuousSignal.MAX_ENTRIES * (x - xStart) / (xEnd - xStart)), y);
    }

    public void setRange(float min, float max){
        yStart = min;
        yEnd = max;
    }

    public void setDomain(float min, float max){
        xStart = min;
        xEnd = max;
    }

    public boolean isHovered(float x, float y) {
        return new Rect((int) this.x, (int) this.y, (int) getX2(), (int) getY2()).contains((int) x, (int) y);
    }

    private float getX2(){
        return x + width;
    }

    private float getY2(){
        return y + height;
    }

    private float getCenterX(){
        return x + width / 2;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return realHeight;
    }

    public float getSomeOtherHeight(){
        return height;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private float getYAxisPosition(){
        if(yStart * yEnd < 0){
            float oneGap = height / (yEnd - yStart);
            float offset = (float) ((Math.ceil(yStart) - yStart) * oneGap);
            return (float) (getY2() - offset - oneGap * Math.floor(-yStart));
        }else{
            if(yEnd == 0){
                return getY() - 5;
            }else{
                return getY2() + 5;
            }
        }
    }

    private float getXAxisPosition(){
        if(xStart * xEnd < 0){
            return getX() + (width) / (xEnd - xStart) * (-xStart);
        }else{
            if(xEnd == 0){
                return getX2() + 5;
            }else{
                return getX() - 5;
            }
        }
    }

    public float getXStart() {
        return xStart;
    }

    public float getXEnd() {
        return xEnd;
    }

    public float getYStart() {
        return yStart;
    }

    public float getYEnd() {
        return yEnd;
    }
}
