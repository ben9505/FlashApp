package com.flashlight.webis.slidetext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.flashlight.webis.support.Variable;


/**
 * Created by Hien on 6/15/2016.
 */
public class Slide {
    private Bitmap hinh;
    private int toado_X = 0;
    private int width;
    private int speed;

    public Slide(Bitmap bitmap , int speed, int width){
        hinh = bitmap;
        this.speed = speed;
        this.width = width;
        toado_X = width;
    }

    public void doDrawRunning(Canvas canvas) {
        //giam toa do de dich chuyen cho nen1
        toado_X = toado_X - speed;

        // tinh do lech cho hinh 2 (xem hinh minh hoa)
        //int toadonen1_phu_X = width - (-toado_X)+100;
        Log.i(Variable.TAG, String.valueOf(toado_X));
        if (toado_X <= (-hinh.getWidth())) {
            toado_X = width;
            canvas.drawBitmap(hinh,width, 0, null);
        }else{
            canvas.drawBitmap(hinh, toado_X, 0, null);
        }

    }

}
