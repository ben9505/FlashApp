package com.flashlight.webis.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.flashlight.webis.R;
import com.flashlight.webis.SlideActivity;
import com.flashlight.webis.support.Deleget;
import com.flashlight.webis.support.Fonts;
import com.flashlight.webis.support.Helper;
import com.flashlight.webis.widget.FlashlightIntentService;

import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

/**
 * Created by Hien on 6/15/2016.
 */
public class TextSlide extends Fragment implements View.OnClickListener {

    View mainView;
    Helper cHelper;
    Fonts cFonts;
    Deleget deleget;
    private boolean isFlag;
    public static TextSlide newInstance(){
        TextSlide slide = new TextSlide();
        return slide;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        deleget = (Deleget) activity;
    }
    LineColorPicker colorPicker;
    EditText editText;
    SeekBar seekBar_speed,seekBar_size;
    Button btnView;
    RelativeLayout frameEdittext;
    int speed,textsize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_textslide,container,false);
        cHelper = Helper.getInstance(getActivity());
        cFonts = Fonts.getInstance(getActivity());
        isFlag = false;
        initPreference();
        initFunction();
        return mainView;
    }
    public void initPreference(){
        colorPicker = (LineColorPicker) mainView.findViewById(R.id.picker);
        editText = (EditText) mainView.findViewById(R.id.editText);
        seekBar_speed = (SeekBar) mainView.findViewById(R.id.seekBar_speed);
        seekBar_speed.getProgressDrawable().setColorFilter(0xFFF1B002, PorterDuff.Mode.SRC_ATOP);
        seekBar_speed.getThumb().setColorFilter(0xFFF1B002, PorterDuff.Mode.SRC_ATOP);
        seekBar_size = (SeekBar) mainView.findViewById(R.id.seekBar_size);
        seekBar_size.getProgressDrawable().setColorFilter(0xFFF1B002, PorterDuff.Mode.SRC_ATOP);
        seekBar_size.getThumb().setColorFilter(0xFFF1B002, PorterDuff.Mode.SRC_ATOP);
        frameEdittext = (RelativeLayout) mainView.findViewById(R.id.frameEdittext);
        frameEdittext.requestLayout();
        btnView = (Button) mainView.findViewById(R.id.btnView);
        btnView.setOnClickListener(this);

    }
    public void OpenWidgetApp(boolean isFlash) {
        Intent intent = new Intent(getActivity(), FlashlightIntentService.class);
        intent.setAction(isFlash ? FlashlightIntentService.ACTION_TURN_ON : FlashlightIntentService.ACTION_TURN_OFF);
        getActivity().startService(intent);
    }
    public void initFunction(){
        btnView.setSelected(true);
        btnView.setTypeface(cFonts.get_RO_BOLD());
        //btnCancel.setTypeface(cFonts.get_RO_BOLD());
        editText.setTextColor(colorPicker.getColor());
        editText.setHintTextColor(colorPicker.getColor());
        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int c) {
                editText.setTextColor(c);
                editText.setHintTextColor(c);
            }
        });
        seekBar_size.setMax(40);
        seekBar_size.setKeyProgressIncrement(2);
        seekBar_size.setProgress(20);
        editText.setTextSize(40);
        textsize = 80;

        seekBar_speed.setMax(20);
        seekBar_speed.setKeyProgressIncrement(2);
        seekBar_speed.setProgress(2);
        speed = 2;
        seekBar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText.setTextSize(i*2);
                textsize = i*4;
                if (i==0) {
                    editText.setTextSize(5);
                    textsize = 10;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void onActionButtonView(){
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            text = "BABOSARANG";
        }
        Intent intent = new Intent(getActivity(), SlideActivity.class);
        intent.putExtra("text",text);
        intent.putExtra("color",colorPicker.getColor());
        intent.putExtra("speed",speed);
        intent.putExtra("textsize",textsize);
        TextSlide.this.startActivity(intent);




    }

    @Override
    public void onClick(View view) {
        if (isFlag) {
            return;
        }isFlag=true;
        switch (view.getId()){
            case R.id.btnView:
                onActionButtonView();
                break;
            default:
                break;
        }
        isFlag=false;
    }


}
