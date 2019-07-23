package com.tinslam.convolution.Base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tinslam.convolution.UI.Button.Button;
import com.tinslam.convolution.Utilities.ConvolutionManager;
import com.tinslam.convolution.States.ConvolutionState;
import com.tinslam.convolution.States.DiscreteConvolutionState;

@SuppressLint("ViewConstructor")
public class Game extends SurfaceView implements SurfaceHolder.Callback{
    private GameThread thread;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static int width, height;
    @SuppressLint("StaticFieldLeak")
    private static Game game = null;

    private State state;

    public Game(Context context, int width, int height){
        super(context);

        Game.context = context;
        Game.width = width;
        Game.height = height;

        getHolder().addCallback(this);

        setFocusable(true);
        thread = new GameThread(getHolder(), this);
        setKeepScreenOn(true);
        switch(ConvolutionManager.getConvolutionManager().getType()){
            case ConvolutionManager.TYPE_CONTINUOUS :
                setState(new ConvolutionState());
                break;

            case ConvolutionManager.TYPE_DISCRETE :
                setState(new DiscreteConvolutionState());
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(game == null){
            game = this;
            GameThread.setRunning(true);
            thread.start();
        }else{
            thread.start();
            GameThread.setRunning(true);
            GameThread.resumeThread();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        GameThread.pauseThread();
    }

    public void update(){
        state.tick();
    }

    public void draw(Canvas canvas){
        try{
            super.draw(canvas);
            state.render(canvas);
            Button.draw(canvas);
        }catch(Exception ignored){thread.setSurfaceHolder(getHolder());}
    }

    public void setState(State state){
        Button.clear();
        if(this.state != null) this.state.cleanUp();
        this.state = state;
        state.init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return state.onActionDownEvent(event);

            case MotionEvent.ACTION_MOVE:
                return state.onActionMoveEvent(event);

            case MotionEvent.ACTION_UP:
                return Button.onActionUp(event.getX(), event.getY()) || state.onActionUpEvent(event);
        }

        return super.onTouchEvent(event);
    }

    public static Context Context() {
        return context;
    }

    public static float density(){ return Game.Context().getResources().getDisplayMetrics().density; }

    public static int getScreenWidth(){
        return width;
    }

    public static int getScreenHeight(){
        return height;
    }
}
