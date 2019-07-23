package com.tinslam.convolution.Utilities;

public class ConvolutionManager{
    private static ConvolutionManager convolutionManager;
    public static final byte TYPE_DISCRETE = 0,
    TYPE_CONTINUOUS = 1;
    public static String inputFunctionCache = "";
    public static float inputDomainStartCache = 0;
    public static float inputDomainEndCache = 0;
    public static String responseFunctionCache = "";
    public static float responseDomainStartCache = 0;
    public static float responseDomainEndCache = 0;

    private byte type;
    private String inputFunction;
    private String responseFunction;
    private float inputDomainStart;
    private float inputDomainEnd;
    private float responseDomainStart;
    private float responseDomainEnd;

    public ConvolutionManager(byte type){
        convolutionManager = this;
        this.type = type;
    }

    public void setDetails(String inputFunction,
                           float inputDomainStart, float inputDomainEnd,
                           String responseFunction,
                           float responseDomainStart, float responseDomainEnd){
        this.inputFunction = inputFunction;
        this.inputDomainStart = inputDomainStart;
        this.inputDomainEnd = inputDomainEnd;
        this.responseFunction= responseFunction;
        this.responseDomainStart = responseDomainStart;
        this.responseDomainEnd = responseDomainEnd;
        inputFunctionCache = inputFunction;
        responseFunctionCache = responseFunction;
        inputDomainStartCache = inputDomainStart;
        inputDomainEndCache = inputDomainEnd;
        responseDomainStartCache = responseDomainStart;
        responseDomainEndCache = responseDomainEnd;
    }

    public static ConvolutionManager getConvolutionManager() {
        return convolutionManager;
    }

    public byte getType() {
        return type;
    }

    public String getInputFunction() {
        return inputFunction;
    }

    public String getResponseFunction() {
        return responseFunction;
    }

    public float getInputDomainStart() {
        return inputDomainStart;
    }

    public float getInputDomainEnd() {
        return inputDomainEnd;
    }

    public float getResponseDomainStart() {
        return responseDomainStart;
    }

    public float getResponseDomainEnd() {
        return responseDomainEnd;
    }
}