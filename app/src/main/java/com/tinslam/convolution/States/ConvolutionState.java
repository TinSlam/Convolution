package com.tinslam.convolution.States;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.tinslam.convolution.Base.Game;
import com.tinslam.convolution.Base.State;
import com.tinslam.convolution.Concepts.Signal.ContinuousSignal;
import com.tinslam.convolution.Concepts.Function;
import com.tinslam.convolution.R;
import com.tinslam.convolution.UI.Button.Button;
import com.tinslam.convolution.UI.Button.RoundButton;
import com.tinslam.convolution.UI.Chart.Chart;
import com.tinslam.convolution.Utilities.ConvolutionManager;

public class ConvolutionState extends State {
    private static final byte INPUT_STATE = 0,
            DRAG_STATE = 2;

    private Chart inputChart;
    private Chart hChart;
    private Chart reverseChart;
    private Chart outputChart;
    private ContinuousSignal inputSignal;
    private ContinuousSignal hSignal;
    private ContinuousSignal reverseSignal;
    private ContinuousSignal multiplicationSignal;
    private ContinuousSignal convolutionSignal;
    private byte state = INPUT_STATE;
    private Chart currentChart;

    private float lastX, lastY;

    private int gap;

    private Button confirmButton;
    @SuppressWarnings("FieldCanBeLocal")
    private Bitmap confirmButtonImage;
    private Button resetButton;
    @SuppressWarnings("FieldCanBeLocal")
    private Bitmap resetButtonImage;

    private byte outputState = 0;

    @Override
    public void init(){
        gap = (int) (32 * Game.density());

        confirmButtonImage = BitmapFactory.decodeResource(Game.Context().getResources(), R.drawable.color_blue);
        resetButtonImage = BitmapFactory.decodeResource(Game.Context().getResources(), R.drawable.color_blue);

        inputChart = new Chart("x(t)");
        inputChart.setPosition(gap, gap, Game.getScreenWidth() - 2 * gap, (Game.getScreenHeight() - 4 * gap) / 2);

        hChart = new Chart("h(t)");
        hChart.setPosition(gap, Game.getScreenHeight() / 2 + gap, Game.getScreenWidth() - 2 * gap, (Game.getScreenHeight() - 4 * gap) / 2);

        reverseChart = new Chart("h(-T + t)");
        reverseChart.setPosition(-2 * Game.getScreenWidth(), 3 * gap + 2 * (Game.getScreenHeight() - 4 * gap) / 3, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3);

        outputChart = new Chart("Convolution");
        outputChart.setPosition(Game.getScreenWidth() * 2, gap, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3);

        inputSignal = new ContinuousSignal();
        hSignal = new ContinuousSignal();
        hSignal.setColor(Color.BLUE);
        reverseSignal = new ContinuousSignal();
        reverseSignal.setColor(Color.GREEN);
        convolutionSignal = new ContinuousSignal();
        multiplicationSignal = new ContinuousSignal();
        convolutionSignal.setColor(Color.MAGENTA);
        multiplicationSignal.setColor(Color.MAGENTA);

        Function inputFunction = new Function(ConvolutionManager.getConvolutionManager().getInputFunction());
        Function responseFunction = new Function(ConvolutionManager.getConvolutionManager().getResponseFunction());
        inputChart.setDomain(ConvolutionManager.getConvolutionManager().getInputDomainStart(),
                ConvolutionManager.getConvolutionManager().getInputDomainEnd());
        hChart.setDomain(ConvolutionManager.getConvolutionManager().getResponseDomainStart(),
                ConvolutionManager.getConvolutionManager().getResponseDomainEnd());

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
            float x = inputChart.getXStart() + (inputChart.getXEnd() - inputChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
            float y = inputFunction.output(x);
            if(y < min) min = y;
            if(y > max) max = y;
            inputSignal.addPoint(i, y);
        }
        max = max + 1;
        min = min - 1;
        inputChart.setRange(min, max);

        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
        for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
            float x = hChart.getXStart() + (hChart.getXEnd() - hChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
            float y = responseFunction.output(x);
            if(y < min) min = y;
            if(y > max) max = y;
            hSignal.addPoint(i, y);
        }
        max = max + 1;
        min = min - 1;
        hChart.setRange(min, max);

        confirmButton = new RoundButton((int) (Game.getScreenWidth() - 32 * Game.density()), (int) (Game.getScreenHeight() - 32 * Game.density()), (int) (32 * Game.density()), confirmButtonImage){
            @Override
            public void performAction() {
                switch(state){
                    case INPUT_STATE :
                        transitionToSecondState();
                        break;

                    case DRAG_STATE :
                        switch(outputState){
                            case 0 :
                                outputChart.setTitle("Multiplication");
                                outputState = 1;
                                prepareChartForMultiplication();
                                break;

                            case 1 :
                                outputChart.setTitle("Convolution");
                                outputState = 0;
                                prepareChartForConvolution();
                                break;
                        }
                        break;
                }
            }
        };

        resetButton = new RoundButton((int) (32 * Game.density()), (int) (Game.getScreenHeight() - 32 * Game.density()), (int) (32 * Game.density()), resetButtonImage){
            @Override
            public void performAction() {
                resetButton.removeButton();
                confirmButton.removeButton();
                state = INPUT_STATE;
                outputState = 0;
                init();
            }
        };
        resetButton.setActive(false);
    }

    private void prepareChartForConvolution(){
        outputChart.setDomain(ConvolutionManager.getConvolutionManager().getInputDomainStart() + ConvolutionManager.getConvolutionManager().getResponseDomainStart(),
                ConvolutionManager.getConvolutionManager().getInputDomainEnd() + ConvolutionManager.getConvolutionManager().getResponseDomainEnd());
        outputChart.setRange(convolutionSignal.min - 1, convolutionSignal.max + 1);
    }

    private void prepareChartForMultiplication() {
        outputChart.setDomain(inputChart.getXStart(), inputChart.getXEnd());
        for(int j = -ContinuousSignal.MAX_ENTRIES; j < ContinuousSignal.MAX_ENTRIES; j++){
            for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
                float x = outputChart.getXStart() + (outputChart.getXEnd() - outputChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
                int x1 = (int) ((x - inputChart.getXStart()) / (inputChart.getXEnd() - inputChart.getXStart()) * ContinuousSignal.MAX_ENTRIES);
                int x2 = (int) ((x - reverseChart.getXStart()) / (reverseChart.getXEnd() - reverseChart.getXStart()) * ContinuousSignal.MAX_ENTRIES);
                multiplicationSignal.addPoint(i, inputSignal.getPoint(x1) * reverseSignal.getShiftedPoint(x2, j + ContinuousSignal.MAX_ENTRIES / 2));
            }
        }
        outputChart.setRange(multiplicationSignal.getMin(), multiplicationSignal.getMax());
    }

    @Override
    public void tick(){
        if(state == DRAG_STATE){
            switch(outputState){
                case 0 :
                    convolutionSignal.setDrawLimit((reverseSignal.getShift() + reverseSignal.getDomainEnd() - inputSignal.getDomainStart()) * (ContinuousSignal.MAX_ENTRIES) / (ContinuousSignal.MAX_ENTRIES - inputSignal.getDomainStart()));
                    break;

                case 1 :
                    for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
                        float x = outputChart.getXStart() + (outputChart.getXEnd() - outputChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
                        int x1 = (int) ((x - inputChart.getXStart()) / (inputChart.getXEnd() - inputChart.getXStart()) * ContinuousSignal.MAX_ENTRIES);
                        int x2 = (int) ((x - reverseChart.getXStart()) / (reverseChart.getXEnd() - reverseChart.getXStart()) * ContinuousSignal.MAX_ENTRIES);
                        multiplicationSignal.addPoint(i, inputSignal.getPoint(x1) * reverseSignal.getPoint(x2));
                    }
                    break;
            }
        }
    }

    @Override
    public void render(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        inputChart.draw(canvas);
        inputSignal.draw(inputChart, canvas);
        hChart.draw(canvas);
        hSignal.draw(hChart, canvas);
        reverseChart.draw(canvas);
        reverseSignal.draw(reverseChart, canvas);
        if(outputState == 0){
            outputChart.draw(canvas, false);
            convolutionSignal.draw(outputChart, canvas);
        }else{
            outputChart.draw(canvas);
            multiplicationSignal.draw(outputChart, canvas);
        }
    }

    private void transitionToSecondState(){
        inputChart.setTitle("x(T)");

        state = DRAG_STATE;

        convolve();

        float left = Math.min(inputChart.getXStart() - hChart.getXEnd() + hChart.getXStart(), -hChart.getXEnd());
        reverseChart.setDomain(left, inputChart.getXEnd() + hChart.getXEnd() - hChart.getXStart());
        float[] newPoint = new float[ContinuousSignal.MAX_ENTRIES];
        for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
            float x = inputChart.getXStart() + (inputChart.getXEnd() - inputChart.getXStart()) * (i) / ContinuousSignal.MAX_ENTRIES;
            float y = inputSignal.getPoint(i);
            int x1 = (int) ((x - (left)) / ((inputChart.getXEnd() + hChart.getXEnd() - hChart.getXStart()) - (left)) * ContinuousSignal.MAX_ENTRIES);
            if(x1 < 0 || x1 >= newPoint.length) continue;
            newPoint[x1] = y;
        }
        inputSignal.setPoints(newPoint);
        inputChart.setDomain(left, inputChart.getXEnd() + hChart.getXEnd() - hChart.getXStart());

        reverseChart.setDomain(inputChart.getXStart(), inputChart.getXEnd());
        synchronized(hSignal.getLock()){
            for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
                float x = hChart.getXStart() + (hChart.getXEnd() - hChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
                float y = hSignal.getPoint(i);
                int x1 = (int) ((-x - reverseChart.getXStart()) / (reverseChart.getXEnd() - reverseChart.getXStart()) * ContinuousSignal.MAX_ENTRIES);
                reverseSignal.addPoint(x1, y);
            }
        }
        reverseChart.setRange(hChart.getYStart(), hChart.getYEnd());

        moveChart(inputChart, gap, 2 * gap + (Game.getScreenHeight() - 4 * gap) / 3, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3, 400);
        moveChart(hChart, -2 * Game.getScreenWidth(), gap, (int) hChart.getWidth(), (int) hChart.getHeight(), 400);
        moveChart(reverseChart, gap, (int) reverseChart.getY(), (int) reverseChart.getWidth(), (int) reverseChart.getHeight(), 400);
        moveChart(outputChart, gap, (int) outputChart.getY(), (int) outputChart.getWidth(), (int) outputChart.getHeight(), 400);

        resetButton.setActive(true);
    }

    private void convolve(){
        convolutionSignal = new ContinuousSignal();
        convolutionSignal.setColor(Color.MAGENTA);
        outputChart.setDomain(ConvolutionManager.getConvolutionManager().getInputDomainStart() + ConvolutionManager.getConvolutionManager().getResponseDomainStart(),
                ConvolutionManager.getConvolutionManager().getInputDomainEnd() + ConvolutionManager.getConvolutionManager().getResponseDomainEnd());

        for(int i = 0; i < ContinuousSignal.MAX_ENTRIES; i++){
            float value = 0;
            float x = outputChart.getXStart() + (outputChart.getXEnd() - outputChart.getXStart()) * i / ContinuousSignal.MAX_ENTRIES;
            for(int j = -ContinuousSignal.MAX_ENTRIES; j < 2 * ContinuousSignal.MAX_ENTRIES; j++){
                float x5 = ConvolutionManager.getConvolutionManager().getInputDomainStart() +
                        (ConvolutionManager.getConvolutionManager().getInputDomainEnd() - ConvolutionManager.getConvolutionManager().getInputDomainStart()) * j / ContinuousSignal.MAX_ENTRIES;
                int x3 = (int) ((x - x5 - ConvolutionManager.getConvolutionManager().getResponseDomainStart()) /
                        (ConvolutionManager.getConvolutionManager().getResponseDomainEnd() - ConvolutionManager.getConvolutionManager().getResponseDomainStart()) * ContinuousSignal.MAX_ENTRIES);
                int x4 = (int) ((x5 - ConvolutionManager.getConvolutionManager().getInputDomainStart()) /
                        (ConvolutionManager.getConvolutionManager().getInputDomainEnd() - ConvolutionManager.getConvolutionManager().getInputDomainStart()) * ContinuousSignal.MAX_ENTRIES);
                value += inputSignal.getPoint(x4) * hSignal.getPoint(x3);
            }
            convolutionSignal.addPoint(i, value);
        }

        outputChart.setRange(convolutionSignal.min - 1, convolutionSignal.max + 1);
    }

    private void moveChart(final Chart chart, final int x2, final int y2, final int width2, final int height2, @SuppressWarnings("SameParameterValue") final int totalTime){
        final int interval = 10;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                float dx = (x2 - chart.getX()) / (totalTime / 10);
                float dy = (y2 - chart.getY()) / (totalTime / 10);
                float dWidth = (width2 - chart.getWidth()) / (totalTime / 10);
                float dHeight = (height2 - chart.getHeight()) / (totalTime / 10);
                while(counter != totalTime){
                    chart.setPosition(chart.getX() + dx, chart.getY() + dy, chart.getWidth() + dWidth, chart.getHeight() + dHeight);
                    try{
                        Thread.sleep(interval);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    counter += interval;
                }
            }
        }).start();
    }

    @Override
    public void cleanUp(){

    }

    @Override
    public boolean onActionUp(float x, float y){
        return true;
    }

    @Override
    public boolean onActionMove(float x, float y){
        Chart chart = null;
        ContinuousSignal signal = null;

        switch(state){
            case DRAG_STATE :
                if(x > Game.getScreenWidth() || x < 0) return true;
                if(reverseChart.isHovered(x, y)){
                    chart = reverseChart;
                    signal = reverseSignal;
                }

                if(chart != null){
                    signal.setShift((int) (-reverseSignal.getDomainStart() + (x - reverseChart.getX()) / reverseChart.getWidth() * ContinuousSignal.MAX_ENTRIES));
                }

                lastX = x;
                lastY = y;
                break;

            case INPUT_STATE :
                if(inputChart.isHovered(x, y)){
                    chart = inputChart;
                    signal = inputSignal;
                }else if(hChart.isHovered(x, y)){
                    chart = hChart;
                    signal = hSignal;
                }

                if(chart != null){
                    if(currentChart != chart){
                        lastX = x;
                        lastY = y;
                    }
                    currentChart = chart;
                    float counter = 0;
                    float interval = 0.1f;
                    if(lastX < x){
                        while(lastX + counter <= x){
                            chart.addPoint(signal, lastX + counter, lastY + counter * (y - lastY) / (x - lastX));
                            counter += interval;
                        }
                    }else{
                        while(lastX + counter >= x){
                            chart.addPoint(signal, lastX + counter, lastY + counter * (y - lastY) / (x - lastX));
                            counter -= interval;
                        }
                    }
                    lastX = x;
                    lastY = y;
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onActionDown(float x, float y){
        lastX = x;
        lastY = y;
        return true;
    }
}
