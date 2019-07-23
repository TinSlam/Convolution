package com.tinslam.convolution.Concepts.Signal;

import android.graphics.Canvas;
import android.graphics.Color;

import com.tinslam.convolution.UI.Chart.Chart;

public class ContinuousSignal extends Signal {
    public static final int MAX_ENTRIES = 500;

    private final Object lock = new Object();
    private float[] points = new float[MAX_ENTRIES];
    private int shift = 0;
    private int drawLimit = ContinuousSignal.MAX_ENTRIES;
    private int domainStart = Integer.MAX_VALUE;
    private int domainEnd = Integer.MIN_VALUE;

    public ContinuousSignal(){
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        for(int i = 0; i < points.length; i++){
            points[i] = 0;
        }
    }

    @Override
    public void draw(Chart chart, Canvas canvas){
        synchronized(lock){
            for(int i = 0; i < drawLimit; i++){
                if(i + shift < 0 || i + shift >= MAX_ENTRIES) continue;
                chart.drawPoint(i + shift, points[i], paint, canvas);
            }
            for(int i = Math.max(0, drawLimit); i < ContinuousSignal.MAX_ENTRIES; i++){
                chart.drawPoint(i + shift, 0, paint, canvas);
            }
            if(shift > 0){
                for(int i = 0; i < shift; i++){
                    chart.drawPoint(i, 0, paint, canvas);
                }
            }else if(shift < 0){
                for(int i = MAX_ENTRIES + shift; i < MAX_ENTRIES; i++){
                    chart.drawPoint(i, 0, paint, canvas);
                }
            }
        }
    }

    @Override
    public void addPoint(int x, float y){
        if(x < 0 || x >= MAX_ENTRIES) return;
        synchronized(lock){
            points[x] = y;
            if(y > max) max = y;
            if(y < min) min = y;
            if(y != 0){
                if(x < domainStart) domainStart = x;
                if(x > domainEnd) domainEnd = x;
            }
        }
    }

    public Object getLock(){
        return lock;
    }

    public void shift(float dx){
        shift += dx;
    }

    @Override
    public float getPoint(int i){
        if(i - shift < 0 || i - shift >= MAX_ENTRIES){
            return 0;
        }
        return points[i - shift];
    }

    public float getShiftedPoint(int i, int shift){
        if(i - shift < 0 || i - shift >= MAX_ENTRIES){
            return 0;
        }
        return points[i - shift];
    }

    public int getShift() {
        return shift;
    }

    public void setDrawLimit(int drawLimit) {
        this.drawLimit = drawLimit;
    }

    public void setPoints(float[] points) {
        this.points = points;
        domainEnd = 0;
        domainStart = MAX_ENTRIES - 1;
        for(int i = 0; i < points.length; i++){
            if(points[i] != 0){
                if(i < domainStart) domainStart = i;
                if(i > domainEnd) domainEnd = i;
            }
        }
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getDomainStart() {
        return domainStart;
    }

    public int getDomainEnd() {
        return domainEnd;
    }
}
