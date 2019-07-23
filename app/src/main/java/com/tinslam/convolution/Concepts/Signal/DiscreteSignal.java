package com.tinslam.convolution.Concepts.Signal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.SparseArray;

import com.tinslam.convolution.UI.Chart.Chart;

public class DiscreteSignal extends Signal {
    private final Object lock = new Object();
    private SparseArray<Float> points = new SparseArray<>();
    private int shift = 0;
    private int drawLimit = Integer.MAX_VALUE;
    private int domainStart = Integer.MAX_VALUE;
    private int domainEnd = Integer.MIN_VALUE;

    public DiscreteSignal(){
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
    }

    @Override
    public void draw(Chart chart, Canvas canvas){
        synchronized(lock){
            for(int i = 0; i < points.size(); i++){
                int x = points.keyAt(i);
                x += shift;
                if(x > drawLimit) continue;
                if(x < chart.getXStart() || x > chart.getXEnd()) continue;
                chart.drawDiscretePoint(x, points.valueAt(i), paint, canvas);
            }
        }
    }

    public void reverse(DiscreteSignal reversedSignal){
        synchronized(lock){
            for(int i = 0; i < points.size(); i++){
                reversedSignal.addPoint(-points.keyAt(i), points.valueAt(i));
            }
        }
    }

    @Override
    public void addPoint(int x, float y){
        synchronized(lock){
            points.put(x, y);
            if(y > max) max = y;
            if(x > domainEnd) domainEnd = x;
            if(y < min) min = y;
            if(x < domainStart) domainStart = x;
        }
    }

    @Override
    public float getPoint(int x){
        return points.get(x);
    }

    public int getShift() {
        return shift;
    }

    public void setDrawLimit(int drawLimit) {
        this.drawLimit = drawLimit;
    }

    public int getDomainStart(){
        return domainStart;
    }

    public int getDomainEnd(){
        return domainEnd;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }
}
