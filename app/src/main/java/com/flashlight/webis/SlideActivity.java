package com.flashlight.webis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashlight.webis.slidetext.Pannel;

/**
 * Created by Hien on 7/12/2016.
 */
public class SlideActivity extends Activity {
    TextView textView;
    FrameLayout flContainSurfaceView;
    int speed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide);
        flContainSurfaceView = (FrameLayout) findViewById(R.id.flContainSurfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        int color = intent.getIntExtra("color", 0);
        speed = intent.getIntExtra("speed", 2);
        int textsize = intent.getIntExtra("textsize", 50);

        Bitmap bm = textAsBitmap(text,textsize,color);
        if (bm!=null) {
            Pannel pannel = new Pannel(getApplicationContext(),bm, speed);
            flContainSurfaceView.addView(pannel);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,bm.getHeight());
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            flContainSurfaceView.setLayoutParams(params);
            flContainSurfaceView.requestLayout();
        }else{
            Toast.makeText(getApplicationContext(),"Can't Create Bitmap!!",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        textSize = textSize *4;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void actionCloseActivity(View view){
        finish();
    }

}
