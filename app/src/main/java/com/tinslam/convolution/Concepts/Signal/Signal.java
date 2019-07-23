package com.tinslam.convolution.Concepts.Signal;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.tinslam.convolution.UI.Chart.Chart;

public abstract class Signal{
    Paint paint = new Paint();
    public float max = Float.MIN_VALUE;
    public float min = Float.MAX_VALUE;

    public abstract void draw(Chart chart, Canvas canvas);
    public abstract void addPoint(int x, float y);
    public abstract float getPoint(int x);

    public void setColor(int color) {
        paint.setColor(color);
    }

    public float getMax(){
        return max;
    }

    public float getMin(){
        return min;
    }
}
