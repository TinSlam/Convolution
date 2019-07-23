
package com.tinslam.convolution.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.tinslam.convolution.Base.Game;
import com.tinslam.convolution.Base.GameThread;
import com.tinslam.convolution.Utilities.Utils;

public class MainActivity extends Activity {
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mainActivity = this;

        mainActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setContent(){
        //noinspection ConstantConditions
        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final Point size = new Point();
        d.getSize(size);

        Game game = new Game(mainActivity, size.x, size.y);
        setContentView(game);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        GameThread.resumeThread();
        GameThread.setRunning(false);
        Utils.restartApp(this);
    }
}