package com.flashlight.webis.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.flashlight.webis.R;
import com.flashlight.webis.customview.TextViewBold;
import com.flashlight.webis.support.Deleget;
import com.flashlight.webis.support.Fonts;
import com.flashlight.webis.support.Helper;
import com.flashlight.webis.support.Variable;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * Created by Hien on 6/14/2016.
 */
public class Screen extends Fragment implements View.OnClickListener {

    View mainView;
    Deleget deleget;
    Helper cHelper;
    Fonts cFonts;
    private MediaPlayer mpintro;
    public static Screen newInstance(){
        Screen screen = new Screen();
        return screen;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        deleget = (Deleget) activity;
    }

    //UI
    ColorPicker picker;
    ImageButton btnZoomOut;
    SeekBar seekBar_bright;
    //Variable to store brightness value
    private int brightness;
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    TextViewBold brightStart, brightEnd;
    RelativeLayout llScreen;
    SVBar svBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_screen,container,false);
        cHelper = Helper.getInstance(getActivity());
        cFonts = Fonts.getInstance(getActivity());
        initPreference();
        initFunction();
        return mainView;
    }
    public void initPreference(){
        picker = (ColorPicker) mainView.findViewById(R.id.picker);
        btnZoomOut = (ImageButton) mainView.findViewById(R.id.btnZoomOut);
        brightStart = (TextViewBold) mainView.findViewById(R.id.bright_start);
        brightEnd = (TextViewBold) mainView.findViewById(R.id.bright_end);
        llScreen = (RelativeLayout) mainView.findViewById(R.id.llScreen);
        svBar = (SVBar) mainView.findViewById(R.id.svbar);
        svBar.setColor(getActivity().getResources().getColor(R.color.text_active));
    }
    public void initFunction(){
        if (cHelper.getPxWidth() < 500) {
            llScreen.animate().translationY(-40).start();
        }
        //cResolver =  getActivity().getContentResolver();
        picker.setShowOldCenterColor(false);
        picker.addSVBar(svBar);
        btnZoomOut.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnZoomOut:
                    cHelper.setColorNumber(picker.getColor());
                    deleget.respond(Variable.OPEN_PREVIEW);
                break;
            default:
                break;
        }
    }
}
