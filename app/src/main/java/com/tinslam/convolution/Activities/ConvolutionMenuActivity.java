package com.tinslam.convolution.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tinslam.convolution.Utilities.ConvolutionManager;
import com.tinslam.convolution.R;
import com.tinslam.convolution.Utilities.Utils;

public class ConvolutionMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution_menu);

        final Activity self = this;

        android.widget.Button continuousButton = findViewById(R.id.convolution_menu_continuous_button);
        continuousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConvolutionManager(ConvolutionManager.TYPE_CONTINUOUS);
                Utils.switchActivity(self, ConvolutionDetailsActivity.class);
            }
        });

        android.widget.Button discreteButton = findViewById(R.id.convolution_menu_discrete_button);
        discreteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConvolutionManager(ConvolutionManager.TYPE_DISCRETE);
                Utils.switchActivity(self, ConvolutionDetailsActivity.class);
            }
        });
    }
}
