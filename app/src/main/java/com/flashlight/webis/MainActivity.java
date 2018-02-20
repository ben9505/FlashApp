package com.flashlight.webis;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flashlight.webis.fragment.Flash;
import com.flashlight.webis.fragment.Screen;
import com.flashlight.webis.fragment.TextSlide;
import com.flashlight.webis.support.CameraPreview;
import com.flashlight.webis.support.Deleget;
import com.flashlight.webis.support.Fonts;
import com.flashlight.webis.support.Helper;
import com.flashlight.webis.support.Variable;
import com.flashlight.webis.widget.FlashLightWidget;
import com.flashlight.webis.widget.FlashlightIntentService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Deleget {


    public Helper cHelper;
    public Fonts cFonts;

    // UI
    @BindView(R.id.btnChooseType) Button btnChooseType;
    @BindView(R.id.btnOpenFlashPage) Button btnOpenFlashPage;
    @BindView(R.id.btnOpenScreenPage) Button btnOpenScreenPage;
    @BindView(R.id.lnHeader) LinearLayout lnHeader;
    @BindView(R.id.rlViewPreview) RelativeLayout rlViewPreview;
    // UI SETTING
    @BindView(R.id.rlSetting_Popup) RelativeLayout rlSetting_Popup;
    @BindView(R.id.swDefault) SwitchButton swDefault;
    @BindView(R.id.swScreenKeep) SwitchButton swScreenKeep;
    @BindView(R.id.swPushAlarm) SwitchButton swPushAlarm;
    @BindView(R.id.btnSetting) ImageButton btnSetting;
    @BindView(R.id.contentView) FrameLayout contentView;
    @BindView(R.id.surfaceContain) FrameLayout preview;

    boolean hasFlash;
    boolean isFristCreateView;
    //Flash
    private boolean mIsFlashOn;
    private Camera mCamera = null;
    private CameraPreview mPreview;
    Camera.Parameters params;
    boolean isThreadRun;
    int delay = Variable.FLASH_ALWAYS_ON;
    SharedPreferences pre;

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        cHelper = Helper.getInstance(getApplicationContext());
        cFonts = Fonts.getInstance(getApplicationContext());
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!hasFlash) {

            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle(getString(R.string.error));
            alert.setMessage(getString(R.string.flash_error));
            alert.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        } else {
            Fragment SlideFragment = getFragmentManager().findFragmentById(R.id.contentView);
            if (!(SlideFragment instanceof TextSlide)) {
                initFragmentFlash(pre.getBoolean(Variable.DEFAULT_STATE, true));
            }

        }

    }

    public void initFragmentFlash(boolean isOpen) {
        btnChooseType.setText(R.string.TOP_FLASHLIGHT);
        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_flash));
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, Flash.newInstance(isOpen), Variable.FRAGMENT_FLASH_TAG);
        transaction.commit();
    }

    public void initFragmentScreen() {
        btnChooseType.setText(R.string.TOP_SCREEN);
        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_flash));
        isThreadRun = false;
        if (FlashLightWidget.lightOn) {
            OpenWidgetApp(false);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, Screen.newInstance(), Variable.FRAGMENT_SCREEN_TAG);
        transaction.commit();
    }

    public void initFragmenTextSlide() {
        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_textslide));
        btnChooseType.setText(R.string.TOP_SCREEN);
        isThreadRun = false;
        if (FlashLightWidget.lightOn) {
            OpenWidgetApp(false);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, TextSlide.newInstance(), Variable.FRAGMENT_TEXTSLIDE_TAG);
        transaction.commit();
    }

    public void getValueSetting() {
        swPushAlarm.setChecked(pre.getBoolean(Variable.PUSH_ALARM, true));
        swScreenKeep.setChecked(pre.getBoolean(Variable.SCREEN_KEEP, false));
        swDefault.setChecked(pre.getBoolean(Variable.DEFAULT_STATE, true));
    }

    //call action by XML layout
    public void activeSaveSetting(View view) {
        updateSetting();
        showDialogSaveSetting();
    }

    private void updateSetting() {
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(Variable.DEFAULT_STATE, swDefault.isChecked());
        editor.putBoolean(Variable.PUSH_ALARM, swPushAlarm.isChecked());
        editor.putBoolean(Variable.SCREEN_KEEP, swScreenKeep.isChecked());
        editor.commit();
    }

    @OnClick({R.id.btnChooseType,R.id.btnOpenFlashPage,R.id.btnOpenScreenPage,R.id.btnSetting})
    public void onClick(View view) {
        if (cHelper.clickTimeDelay()) {
            return;
        }
        final int id = view.getId();
        switch (id) {
            case R.id.btnChooseType:
            case R.id.btnOpenFlashPage:
                changeStatusApp();
                break;
            case R.id.btnOpenScreenPage:
                initFragmenTextSlide();
                break;
            case R.id.btnSetting:
            case R.id.rlViewPreview:
                getValueSetting();
                openOrClosePopupSetting();
                break;
        }
    }

    public void openOrClosePopupSetting() {
        rlSetting_Popup.setVisibility(btnSetting.isSelected() ? View.GONE : View.VISIBLE);
        btnSetting.setSelected(btnSetting.isSelected() ? false : true);
    }

    public void changeStatusApp() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.contentView);
        if (fragment instanceof Flash) {
            initFragmentScreen();
        } else {
            initFragmentFlash(false);
        }
    }

    Handler handler = new Handler();

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);


        Fragment SlideFragment = getFragmentManager().findFragmentByTag(Variable.FRAGMENT_TEXTSLIDE_TAG);
        if (SlideFragment instanceof TextSlide) {
            return;
        }
        isThreadRun = false;



        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!pre.getBoolean(Variable.SCREEN_KEEP, false)) {
            if (FlashLightWidget.lightOn) {
                OpenWidgetApp(false);
            }
        } else {

            if (FlashLightWidget.lightOn) {
                OpenWidgetApp(false);
            }

            Fragment fragment = getFragmentManager().findFragmentById(R.id.contentView);
            if (fragment instanceof Flash) {
                Flash flash = (Flash) fragment;
                if (flash.checkBooleanFlash()) {
                    OpenWidgetApp(true);
                }
            }






        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        System.gc();
    }

    public void showDialogSaveSetting() {
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setTitle("Successful");
        alert.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {

        if (btnSetting.isSelected()) {
            openOrClosePopupSetting();
            return;
        }


        finish();

    }


    @Override
    public void respond(int i) {
        switch (i) {
            case Variable.OPEN_PREVIEW:
                Log.i(Variable.TAG, String.valueOf(i));
                rlViewPreview.setBackgroundColor(cHelper.getColorNumber());
                rlViewPreview.setVisibility(View.VISIBLE);
                break;
            case Variable.INIT_CAMERA:
                initCamera();
                break;
            case Variable.FLASH_ON:
                isThreadRun = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isThreadRun) {
                            delay = cHelper.getDelay();
                            isThreadRun = true;
                            runThread();
                        }
                    }
                },500);

                break;
            case Variable.FLASH_OFF:
                isThreadRun = false;
                break;
            default:
                break;
        }
    }


    public void initCamera() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
    }

    public void setDelay(int pDelay) {
        delay = pDelay;
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public void runThread() {
        Thread t = new Thread() {
            public void run() {
                try {

                    while (isThreadRun) {
                        Log.i(Variable.TAG, "Delay:" + String.valueOf(delay));
                        if (delay == Variable.FLASH_ALWAYS_ON) {
                            if (!mIsFlashOn) {
                                turnOn();
                            }
                        } else {
                            toggleFlashLight();
                            sleep(delay);
                        }
                    }
                    isThreadRun = false;
                    turnOff();

                } catch (Exception e) {
                    e.printStackTrace();
                    isThreadRun = false;
                    runThread();
                    Log.i(Variable.TAG, "Error Thread");
                }
            }
        };
        t.start();
    }

    public void turnOn() {
        params = mCamera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(params);
        mCamera.startPreview();
        mIsFlashOn = true;
    }

    /**
     * Turn the devices FlashLight off
     */
    public void turnOff() {
        // Turn off flashlight
        params = mCamera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
        mIsFlashOn = false;
        mCamera.startPreview();

    }

    public void setIsFirstStart(boolean isFirst){
        isFristCreateView = isFirst;
    }

    public boolean isFirstCreate(){
        return isFristCreateView;
    }

    /**
     * Toggle the flashlight on/off status
     */
    public void toggleFlashLight() {
        if (!mIsFlashOn) { // Off, turn it on
            turnOn();
        } else { // On, turn it off
            turnOff();
        }
    }

    public void OpenWidgetApp(boolean isFlash) {
        Intent intent = new Intent(getApplicationContext(), FlashlightIntentService.class);
        intent.setAction(isFlash ? FlashlightIntentService.ACTION_TURN_ON : FlashlightIntentService.ACTION_TURN_OFF);
        startService(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
    }}
