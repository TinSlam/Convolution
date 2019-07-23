package com.tinslam.convolution.Base;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class State{
    public abstract void init();
    public abstract void tick();
    public abstract void render(Canvas canvas);
    public abstract void cleanUp();
    public abstract boolean onActionUp(float x, float y);
    public abstract boolean onActionMove(float x, float y);
    public abstract boolean onActionDown(float x, float y);

    public boolean onActionUpEvent(MotionEvent event){
        return onActionUp(event.getX(), event.getY());
    }

    public boolean onActionMoveEvent(MotionEvent event){
        return onActionMove(event.getX(), event.getY());
    }

    public boolean onActionDownEvent(MotionEvent event){
        return onActionDown(event.getX(), event.getY());
    }
}