package com.tinslam.convolution.UI.Button;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class RoundButton extends Button {
    private int x, y;
    private int radius;
    private Bitmap image;

    protected RoundButton(int x, int y, int radius, Bitmap image){
        this.image = Bitmap.createScaledBitmap(image, radius * 2, radius * 2, false);

        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public boolean isHovered(float x, float y) {
        return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) <= radius;
    }

    @Override
    public void render(Canvas canvas){
        canvas.drawBitmap(image, getLeft(), getTop(), null);
    }

    private int getLeft(){
        return x - radius;
    }

    private int getTop(){
        return y - radius;
    }
}
