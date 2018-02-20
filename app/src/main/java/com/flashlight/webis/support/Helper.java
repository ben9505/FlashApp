package com.flashlight.webis.support;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by Hien on 6/9/2016.
 */
public class Helper {

    private static Helper instance = null;
    private Context context;
    protected Helper(Context context) {
        this.context = context;
        getScreenDevice();
    }
    public synchronized static Helper getInstance(Context context) {
        if(instance == null) {
            instance = new Helper(context);
        }
        return instance;
    }

    float pxWidth = 0;
    float pxHeight = 0;
    float dpWidth = 0;
    float dpHeight = 0;
    long lastClickTime = 0;

    boolean sound;

    int colorNumber;

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public int getColorNumber() { return colorNumber; }

    public void setColorNumber(int colorNumber) { this.colorNumber = colorNumber; }

    public float getPxWidth() {
        return pxWidth;
    }

    public float getPxHeight() {
        return pxHeight;
    }

    public float getDpWidth() {
        return dpWidth;
    }

    public float getDpHeight() {
        return dpHeight;
    }

    public void getScreenDevice(){
        pxHeight = context.getResources().getSystem().getDisplayMetrics().heightPixels;
        pxWidth = context.getResources().getSystem().getDisplayMetrics().widthPixels;
        dpHeight = convertPixelsToDp(pxHeight);
        dpWidth = convertPixelsToDp(pxWidth);

        Log.i(Variable.TAG, String.valueOf(pxHeight));
        Log.i(Variable.TAG, String.valueOf(pxWidth));
        Log.i(Variable.TAG, String.valueOf(dpHeight));
        Log.i(Variable.TAG, String.valueOf(dpWidth));
    }

    public float convertDpToPixel(float dp){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public float convertPixelsToDp(float px){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    int delay = Variable.FLASH_ALWAYS_ON;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean clickTimeDelay() {
        return clickTimeDelay(1000);
    }

    public boolean clickTimeDelay(int delay) {
        if (lastClickTime >= System.currentTimeMillis() - delay) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }

}
