package com.tinslam.convolution.UI.Button;

import android.graphics.Canvas;

import java.util.ArrayList;

public abstract class Button{
    private static final Object lock = new Object();
    private static ArrayList<Button> buttons = new ArrayList<>();

    private boolean active = true;

    public Button(){
        synchronized(lock){
            buttons.add(this);
        }
    }

    public abstract void performAction();

    public abstract boolean isHovered(float x, float y);

    public abstract void render(Canvas canvas);

    public static void draw(Canvas canvas){
        synchronized(lock){
            for(Button button : buttons){
                if(button.active) button.render(canvas);
            }
        }
    }

    public static boolean onActionUp(float x, float y){
        synchronized(lock){
            for(Button button : buttons){
                if(button.active){
                    if(button.isHovered(x, y)){
                        button.performAction();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeButton(){
        synchronized(lock){
            for(final Button button : buttons){
                if(button == this){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized(lock){
                                buttons.remove(button);
                            }
                        }
                    }).start();
                    return;
                }
            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static void clear(){
        synchronized(lock){
            buttons.clear();
        }
    }
}