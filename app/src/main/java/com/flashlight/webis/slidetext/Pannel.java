package com.flashlight.webis.slidetext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.flashlight.webis.support.Helper;


/**
 * Created by Hien on 6/15/2016.
 */
public class Pannel extends SurfaceView implements SurfaceHolder.Callback {

    Slide myObject;
    private MainThread thread;
    Helper cHelper;
    public Pannel(Context context, Bitmap bitmap, int speed) {
        super(context);
        getHolder().addCallback(this);
        cHelper = Helper.getInstance(context);
        thread = new MainThread(getHolder(),this);
        myObject = new Slide(bitmap, speed, (int) cHelper.getPxHeight());
        setFocusable(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        myObject.doDrawRunning(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(thread.isAlive()){
            thread.setRunning(false);
        }
    }

    public void cancelProcess(){
        if(thread.isAlive()){
            thread.setRunning(false);
        }
    }
}
