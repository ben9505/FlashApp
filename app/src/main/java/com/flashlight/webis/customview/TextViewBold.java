package com.flashlight.webis.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Hien on 7/12/2016.
 */
public class TextViewBold extends TextView {

    public TextViewBold(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        this.setTypeface(face);
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        this.setTypeface(face);
    }
}
