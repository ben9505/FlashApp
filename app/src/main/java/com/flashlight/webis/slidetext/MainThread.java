package com.flashlight.webis.slidetext;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Hien on 6/15/2016.
 */
public class MainThread extends Thread {
    private boolean running;
    private SurfaceHolder surfaceholder;
    private Pannel gamepanel;

    public MainThread(SurfaceHolder surfaceholder, Pannel gamepanel)
    {
        this.surfaceholder=surfaceholder;
        this.gamepanel=gamepanel;
    }

    public void setRunning(boolean r)
    {
        running=r;

    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {

        super.run();
        Canvas canvas=null;
        while(running)
        {
            //1.cap nhat lai trang thai game //2.render du lieu ra man hinh
            canvas=surfaceholder.lockCanvas();
            if(canvas!=null)
            {
                gamepanel.onDraw(canvas);
                surfaceholder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
