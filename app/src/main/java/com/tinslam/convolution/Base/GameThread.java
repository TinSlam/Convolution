package com.tinslam.convolution.Base;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread{
    private static final int maxFps = 30;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private static boolean running;
    private static final Object lock = new Object();
    private static boolean paused = false;

    GameThread(SurfaceHolder surfaceHolder, Game game){
        super();
        this.surfaceHolder = surfaceHolder;
        this.game = game;
    }

    public static void pauseThread(){
        paused = true;
    }

    public static void resumeThread(){
        paused = false;
        synchronized(lock){
            lock.notify();
        }
    }

    @Override
    public void run(){
        long startTime;
        long timeMillis;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/maxFps;

        while(running){
            if(paused){
                synchronized(lock){
                    try{
                        lock.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            startTime = System.nanoTime();
            Canvas canvas = null;

            try{
                canvas = surfaceHolder.lockCanvas();
                //noinspection SynchronizeOnNonFinalField
                synchronized (surfaceHolder){
                    game.update();
                    game.draw(canvas);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;
            try{
                if(waitTime > 0){
                    sleep(waitTime);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if(frameCount == maxFps){
                double averageFps = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println("Average FPS : " + averageFps);
            }
        }
    }

    public static void setRunning(boolean val){
        running = val;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }
}