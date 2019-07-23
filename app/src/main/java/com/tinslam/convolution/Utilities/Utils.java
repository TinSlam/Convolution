package com.tinslam.convolution.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.tinslam.convolution.Activities.MainActivity;

public class Utils{
    public static void restartApp(Activity activity){
        Intent i = activity.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( activity.getBaseContext().getPackageName() );
        assert i != null;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

    public static void switchActivity(final Activity currentActivity, final Class<?> destinationClass){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Intent intent = new Intent(currentActivity, destinationClass);
                    currentActivity.startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(currentActivity.getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                try{
                    if(currentActivity.getClass() == MainActivity.class) currentActivity.finish();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
