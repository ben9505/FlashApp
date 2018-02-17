package com.flashlight.webis.support;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Hien on 6/9/2016.
 */
public class Fonts {
    private static Fonts instance = null;
    private Context context;
    protected Fonts(Context context) {
        this.context = context;
        initFont();
    }
    public static Fonts getInstance(Context context) {
        if(instance == null) {
            instance = new Fonts(context);
        }
        return instance;
    }

    Typeface _RO_BOLD;

    public void initFont(){
        _RO_BOLD = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");

    }


    public Typeface get_RO_BOLD() {
        return _RO_BOLD;
    }
}
