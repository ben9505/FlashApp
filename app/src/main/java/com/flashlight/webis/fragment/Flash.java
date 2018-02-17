package com.flashlight.webis.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.flashlight.webis.MainActivity;
import com.flashlight.webis.R;
import com.flashlight.webis.customview.TextViewBold;
import com.flashlight.webis.support.Deleget;
import com.flashlight.webis.support.Helper;
import com.flashlight.webis.support.Variable;
import com.flashlight.webis.widget.FlashLightWidget;
import com.flashlight.webis.widget.FlashlightIntentService;
import com.triggertrap.seekarc.SeekArc;

/**
 * Created by Hien on 6/10/2016.
 */
public class Flash extends Fragment implements View.OnClickListener {

    View mainView;
    // Delegate Interface Communication
    public Deleget deleget;
    //support
    Helper cHelper;

    //UI
    ImageButton btnFlash;
    private boolean isFlag;
    int delay = Variable.FLASH_ALWAYS_ON;
    SeekArc seekBar_speed;
    //
    TextViewBold speedStart, speedEnd;
    FrameLayout containSpeekSeekArc;

    Handler handler = new Handler();

    public static Flash newInstance(boolean isOpen){
        Flash flash = new Flash();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Variable.STATUSofFlash, isOpen);
        flash.setArguments(bundle);
        return flash;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        deleget = (Deleget) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_flash,container,false);
        cHelper = Helper.getInstance(getActivity());
        initPreference();
        ((MainActivity) getActivity()).setDelay(Variable.FLASH_ALWAYS_ON);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final Bundle bundle = getArguments();
        if (bundle.getBoolean(Variable.STATUSofFlash, false)) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences pre= PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (pre.getBoolean(Variable.SCREEN_KEEP,false)) {
                        delay = pre.getInt(Variable.DELAY,100000);
                        seekBar_speed.setProgress(0);
                    }
                    runWhenStatusOpen();
                }
            },500);
        }else{
            if (FlashLightWidget.lightOn) {
                OpenWidgetApp(false);
            }

        }



    }




    public void initPreference(){
        containSpeekSeekArc = (FrameLayout) mainView.findViewById(R.id.containSpeekSeekArc);
        speedStart = (TextViewBold) mainView.findViewById(R.id.speedStart);
        speedEnd = (TextViewBold) mainView.findViewById(R.id.speedEnd);
        btnFlash = (ImageButton) mainView.findViewById(R.id.btnFlash);
        btnFlash.setSelected(false);
        btnFlash.setOnClickListener(this);

        seekBar_speed = (SeekArc) mainView.findViewById(R.id.seekBar_speed);
        seekBar_speed.setProgress(0);
        seekBar_speed.setOnSeekArcChangeListener(new onSeekArcChange());
    }

    public void runWhenStatusOpen(){
        btnFlash.setSelected(true);
        if (!FlashLightWidget.lightOn){
            if (!((MainActivity)getActivity()).isFirstCreate()) {
                ((MainActivity)getActivity()).setIsFirstStart(true);
                deleget.respond(Variable.INIT_CAMERA);
                deleget.respond(Variable.FLASH_ON);
            }
        }
        //OpenWidgetApp(true);
    }



    @Override
    public void onClick(View view) {
        if (isFlag){
            return;
        }isFlag = true;



        if (!((MainActivity)getActivity()).isFirstCreate()) {
            if (FlashLightWidget.lightOn) {
                OpenWidgetApp(false);
                btnFlash.setSelected(false);
                isFlag = false;
                return;
            }
            ((MainActivity)getActivity()).setIsFirstStart(true);
            deleget.respond(Variable.INIT_CAMERA);
        }
        deleget.respond(btnFlash.isSelected()? Variable.FLASH_OFF : Variable.FLASH_ON);
        btnFlash.setSelected(btnFlash.isSelected()? false : true);





        //MethodCallByButton();
        isFlag = false;
    }

    public boolean checkBooleanFlash(){
        return btnFlash.isSelected();
    }

    public void MethodCallByButton() {
        try {
            //deleget.respond(Variable.FLASH_OFF);
            OpenWidgetApp(btnFlash.isSelected()? false : true);
            btnFlash.setSelected(btnFlash.isSelected()? false : true);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void OpenWidgetApp(boolean isFlash){
        Intent intent = new Intent(getActivity(), FlashlightIntentService.class);
        intent.setAction(isFlash? FlashlightIntentService.ACTION_TURN_ON : FlashlightIntentService.ACTION_TURN_OFF);
        getActivity().startService(intent);
    }






    public class onSeekArcChange implements SeekArc.OnSeekArcChangeListener{

        @Override
        public void onProgressChanged(SeekArc seekArc, int progress, boolean b) {



        }

        @Override
        public void onStartTrackingTouch(SeekArc seekArc) {

        }

        @Override
        public void onStopTrackingTouch(SeekArc seekArc) {

            delay = 450 - (50 * seekArc.getProgress());
            if (seekArc.getProgress()==0) {
                delay=Variable.FLASH_ALWAYS_ON;
            }else{
                if (FlashLightWidget.lightOn) {
                    OpenWidgetApp(false);
                }
            }
            //((MainActivity) getActivity()).setDelay(delay);
            cHelper.setDelay(delay);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (checkBooleanFlash()) {
                        if (!((MainActivity)getActivity()).isFirstCreate()) {
                            ((MainActivity)getActivity()).setIsFirstStart(true);
                            deleget.respond(Variable.INIT_CAMERA);
                        }
                        deleget.respond(Variable.FLASH_ON);
                    }
                }
            },500);



        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }
}
