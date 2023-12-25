package com.lab1.gptwrapper;

import android.app.Application;

public class App extends Application {
    public static App instance;

    //это надо хранить в бд
    private String openAiAPIkey = "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";
    private double temperature = 0.5;
    //----
    private OpenAIAPI openAIAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        openAIAPI = new OpenAIAPI(openAiAPIkey);
    }

    public static App getInstance() {return instance;}
    public OpenAIAPI getopenAIAPI() {return openAIAPI;}
    public String getOpenAiAPIkey() {return this.openAiAPIkey;}
    public void setOpenAiAPIkey(String openAiAPIkey) {
        this.openAiAPIkey = openAiAPIkey;
        this.openAIAPI = new OpenAIAPI(openAiAPIkey);
    }
    public double getTemperature() {return temperature;}
    public void setTemperature(double temperature) {this.temperature = temperature;}
}

