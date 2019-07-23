package com.tinslam.convolution.States;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.tinslam.convolution.Base.Game;
import com.tinslam.convolution.Base.State;
import com.tinslam.convolution.Concepts.Signal.DiscreteSignal;
import com.tinslam.convolution.Concepts.Function;
import com.tinslam.convolution.R;
import com.tinslam.convolution.UI.Button.Button;
import com.tinslam.convolution.UI.Button.RoundButton;
import com.tinslam.convolution.UI.Chart.Chart;
import com.tinslam.convolution.Utilities.ConvolutionManager;

public class DiscreteConvolutionState extends State {
    private static final byte INPUT_STATE = 0,
            DRAG_STATE = 2;

    private Chart inputChart;
    private Chart hChart;
    private Chart reverseChart;
    private Chart outputChart;
    private DiscreteSignal inputSignal;
    private DiscreteSignal hSignal;
    private DiscreteSignal reverseSignal;
    private DiscreteSignal multiplicationSignal;
    private DiscreteSignal convolutionSignal;
    private byte state = INPUT_STATE;

    private int chosenPoint;

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

        inputChart = new Chart("x[n]");
        inputChart.setPosition(gap, gap, Game.getScreenWidth() - 2 * gap, (Game.getScreenHeight() - 4 * gap) / 2);

        hChart = new Chart("h[n]");
        hChart.setPosition(gap, Game.getScreenHeight() / 2 + gap, Game.getScreenWidth() - 2 * gap, (Game.getScreenHeight() - 4 * gap) / 2);

        reverseChart = new Chart("h[-n + k]");
        reverseChart.setPosition(-2 * Game.getScreenWidth(), 3 * gap + 2 * (Game.getScreenHeight() - 4 * gap) / 3, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3);

        outputChart = new Chart("Convolution");
        outputChart.setPosition(Game.getScreenWidth() * 2, gap, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3);

        inputSignal = new DiscreteSignal();
        hSignal = new DiscreteSignal();
        hSignal.setColor(Color.BLUE);
        reverseSignal = new DiscreteSignal();
        reverseSignal.setColor(Color.GREEN);
        convolutionSignal = new DiscreteSignal();
        multiplicationSignal = new DiscreteSignal();
        convolutionSignal.setColor(Color.MAGENTA);
        multiplicationSignal.setColor(Color.MAGENTA);

        Function inputFunction = new Function(ConvolutionManager.getConvolutionManager().getInputFunction());
        Function responseFunction = new Function(ConvolutionManager.getConvolutionManager().getResponseFunction());
        inputChart.setDomain(ConvolutionManager.getConvolutionManager().getInputDomainStart(),
                ConvolutionManager.getConvolutionManager().getInputDomainEnd());
        hChart.setDomain(ConvolutionManager.getConvolutionManager().getResponseDomainStart(),
                ConvolutionManager.getConvolutionManager().getResponseDomainEnd());

        for(int i = (int) Math.ceil(inputChart.getXStart()); i <= (int) Math.floor(inputChart.getXEnd()); i++){
            inputSignal.addPoint(i, inputFunction.output(i));
        }
        for(int i = (int) Math.ceil(hChart.getXStart()); i <= (int) Math.floor(hChart.getXEnd()); i++){
            hSignal.addPoint(i, responseFunction.output(i));
        }

        inputChart.setRange(inputSignal.min - 1, inputSignal.max + 1);
        hChart.setRange(hSignal.min - 1, hSignal.max + 1);

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
        convolve();
    }

    private void prepareChartForMultiplication() {
        multiplicationSignal = new DiscreteSignal();
        multiplicationSignal.setColor(Color.MAGENTA);
        outputChart.setDomain(inputChart.getXStart(), inputChart.getXEnd());
        int shiftEnd = inputSignal.getDomainEnd() - inputSignal.getDomainStart() + reverseSignal.getDomainEnd() - reverseSignal.getDomainStart();
        int first = inputSignal.getDomainStart();
        int last = inputSignal.getDomainEnd();
        for(int j = 0; j < shiftEnd; j++){
            for(int i = first; i <= last; i++){
                try{
                    multiplicationSignal.addPoint(i, inputSignal.getPoint(i) * reverseSignal.getPoint(i - j));
                }catch(Exception ignored){}
            }
        }
        outputChart.setRange(multiplicationSignal.min - 1, multiplicationSignal.max + 1);
    }

    @Override
    public void tick(){
        if(state == DRAG_STATE){
            switch(outputState){
                case 0 :
                    convolutionSignal.setDrawLimit(reverseSignal.getShift());
                    break;

                case 1 :
                    multiplicationSignal = new DiscreteSignal();
                    multiplicationSignal.setColor(Color.MAGENTA);
                    int first = inputSignal.getDomainStart();
                    int last = inputSignal.getDomainEnd();
                    for(int i = first; i <= last; i++){
                        try{
                            multiplicationSignal.addPoint(i, inputSignal.getPoint(i) * reverseSignal.getPoint(i - reverseSignal.getShift()));
                        }catch(Exception ignored){}
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
        outputChart.draw(canvas);
        if(outputState == 0){
            convolutionSignal.draw(outputChart, canvas);
        }else{
            multiplicationSignal.draw(outputChart, canvas);
        }
    }

    private void transitionToSecondState(){
        inputChart.setTitle("x[k]");

        state = DRAG_STATE;

        float left = Math.min(inputChart.getXStart() - hChart.getXEnd() + hChart.getXStart(), -hChart.getXEnd()) - 1;
        reverseChart.setDomain(left, inputChart.getXEnd() + hChart.getXEnd() - hChart.getXStart() + 1);
        inputChart.setDomain(left, inputChart.getXEnd() + hChart.getXEnd() - hChart.getXStart() + 1);
        hSignal.reverse(reverseSignal);
        reverseChart.setRange(hChart.getYStart(), hChart.getYEnd());

        convolve();
        moveChart(inputChart, gap, 2 * gap + (Game.getScreenHeight() - 4 * gap) / 3, Game.getScreenWidth() - 2 * gap,(Game.getScreenHeight() - 4 * gap) / 3, 400);
        moveChart(hChart, -2 * Game.getScreenWidth(), gap, (int) hChart.getWidth(), (int) hChart.getHeight(), 400);
        moveChart(reverseChart, gap, (int) reverseChart.getY(), (int) reverseChart.getWidth(), (int) reverseChart.getHeight(), 400);
        moveChart(outputChart, gap, (int) outputChart.getY(), (int) outputChart.getWidth(), (int) outputChart.getHeight(), 400);

        resetButton.setActive(true);
    }

    private void convolve(){
        convolutionSignal = new DiscreteSignal();
        convolutionSignal.setColor(Color.MAGENTA);
        int first = hSignal.getDomainStart() + inputSignal.getDomainStart();
        int last = hSignal.getDomainEnd() + inputSignal.getDomainEnd();
        boolean started = false;
        for(int i = first; i <= last; i++){
            float value = 0;
            for(int j = inputSignal.getDomainStart(); j <= inputSignal.getDomainEnd(); j++){
                try{
                    float n1 = inputSignal.getPoint(j);
                    float n2 = hSignal.getPoint(i - j);
                    value += n1 * n2;
                }catch(Exception ignored){}
            }
            if(!started){
                if(value != 0){
                    started = true;
                    convolutionSignal.addPoint(i, value);
                }
            }else{
                convolutionSignal.addPoint(i, value);
            }
        }
        outputChart.setDomain(convolutionSignal.getDomainStart() - 1, convolutionSignal.getDomainEnd() + 1);
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
        DiscreteSignal signal = null;

        switch(state){
            case DRAG_STATE :
                if(x > Game.getScreenWidth() || x < 0) return true;
                if(reverseChart.isHovered(x, y)){
                    chart = reverseChart;
                    signal = reverseSignal;
                }

                if(chart != null){
                     signal.setShift((int) ((x - reverseChart.getX()) / (reverseChart.getWidth() / (reverseChart.getXEnd() - reverseChart.getXStart())) + chart.getXStart() - signal.getDomainStart() - 1));
                }
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
                    signal.addPoint(chosenPoint, chart.getYEnd() - ((y - chart.getY()) * (chart.getYEnd() - chart.getYStart())) / chart.getSomeOtherHeight());
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onActionDown(float x, float y){

        if(state == INPUT_STATE){
            Chart chart = null;
            if(inputChart.isHovered(x, y)){
                chart = inputChart;
            }else if(hChart.isHovered(x, y)){
                chart = hChart;
            }
            if(chart != null){
                chosenPoint = (int) (chart.getXStart() + Math.round((x - chart.getX()) * (chart.getXEnd() - chart.getXStart()) / (chart.getWidth())));
            }
        }
        return true;
    }
}
