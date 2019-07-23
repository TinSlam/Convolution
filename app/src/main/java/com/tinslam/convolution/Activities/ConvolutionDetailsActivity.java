package com.tinslam.convolution.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tinslam.convolution.Utilities.ConvolutionManager;
import com.tinslam.convolution.R;
import com.tinslam.convolution.Utilities.Utils;

public class ConvolutionDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution_details);

        final Activity self = this;

        ConstraintLayout responseFunctionLayout = findViewById(R.id.response_function_layout);
        TextView functionName = responseFunctionLayout.findViewById(R.id.input_function_textview);
        functionName.setText(R.string.response_function);
        final EditText responseFunction = responseFunctionLayout.findViewById(R.id.input_function_textbox);
        final EditText responseDomainStart = responseFunctionLayout.findViewById(R.id.input_domain_start_textbox);
        final EditText responseDomainEnd = responseFunctionLayout.findViewById(R.id.input_domain_end_textbox);

        ConstraintLayout inputFunctionLayout = findViewById(R.id.input_function_layout);
        final EditText inputFunction = inputFunctionLayout.findViewById(R.id.input_function_textbox);
        final EditText inputDomainStart = inputFunctionLayout.findViewById(R.id.input_domain_start_textbox);
        final EditText inputDomainEnd = inputFunctionLayout.findViewById(R.id.input_domain_end_textbox);

        if(ConvolutionManager.responseDomainStartCache != 0) responseDomainStart.setText(String.valueOf(ConvolutionManager.responseDomainStartCache));
        if(ConvolutionManager.responseDomainEndCache != 0) responseDomainEnd.setText(String.valueOf(ConvolutionManager.responseDomainEndCache));
        if(ConvolutionManager.inputDomainStartCache != 0) inputDomainStart.setText(String.valueOf(ConvolutionManager.inputDomainStartCache));
        if(ConvolutionManager.inputDomainEndCache != 0) inputDomainEnd.setText(String.valueOf(ConvolutionManager.inputDomainEndCache));
        if(!ConvolutionManager.responseFunctionCache.isEmpty()) responseFunction.setText(ConvolutionManager.responseFunctionCache);
        if(!ConvolutionManager.inputFunctionCache.isEmpty()) inputFunction.setText(ConvolutionManager.inputFunctionCache);

        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.switchActivity(self, MainActivity.class);
                String input = "1";
                float inputDomainStartNumber = 0;
                float inputDomainEndNumber = 5;
                String response = "1";
                float responseDomainStartNumber = 0;
                float responseDomainEndNumber = 5;
                if(!inputFunction.getText().toString().isEmpty()) input = inputFunction.getText().toString();
                if(!inputDomainStart.getText().toString().isEmpty()) inputDomainStartNumber = Float.parseFloat(inputDomainStart.getText().toString());
                if(!inputDomainEnd.getText().toString().isEmpty()) inputDomainEndNumber = Float.parseFloat(inputDomainEnd.getText().toString());
                if(!responseFunction.getText().toString().isEmpty()) response = responseFunction.getText().toString();
                if(!responseDomainStart.getText().toString().isEmpty()) responseDomainStartNumber = Float.parseFloat(responseDomainStart.getText().toString());
                if(!responseDomainEnd.getText().toString().isEmpty()) responseDomainEndNumber = Float.parseFloat(responseDomainEnd.getText().toString());
                ConvolutionManager.getConvolutionManager().setDetails(
                    input,
                        inputDomainStartNumber,
                        inputDomainEndNumber,
                        response,
                        responseDomainStartNumber,
                        responseDomainEndNumber
                );
            }
        });
    }
}
