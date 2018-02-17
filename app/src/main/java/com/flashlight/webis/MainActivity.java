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

public class MainActivity extends AppCompatActivity implements Deleget {


    public Helper cHelper;
    public Fonts cFonts;

    //flag variable that check process complete
    private boolean isFlag;
    // UI
    Button btnChooseType;
    Button btnOpenFlashPage, btnOpenScreenPage;
    LinearLayout lnHeader;
    RelativeLayout rlViewPreview;
    // UI SETTING
    RelativeLayout rlSetting_Popup;
    SwitchButton swDefault, swScreenKeep, swPushAlarm;
    ImageButton btnSetting;
    boolean hasFlash;
    FrameLayout contentView, preview;

    boolean isFristCreateView;
    //Flash
    private boolean mIsFlashOn;
    private Camera mCamera = null;
    private CameraPreview mPreview;
    Camera.Parameters params;
    boolean isThreadRun;
    int delay = Variable.FLASH_ALWAYS_ON;


    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        cHelper = Helper.getInstance(getApplicationContext());
        cFonts = Fonts.getInstance(getApplicationContext());

        initReference();
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initFunction();
        if (!hasFlash) {

            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        } else {
            Fragment SlideFragment = getFragmentManager().findFragmentByTag(Variable.FRAGMENT_TEXTSLIDE_TAG);
            if (!(SlideFragment instanceof TextSlide)) {
                initFragmentFlash(pre.getBoolean(Variable.DEFAULT_STATE, true));
            }

        }

    }

    public void initFragmentFlash(boolean isOpen) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, Flash.newInstance(isOpen), Variable.FRAGMENT_FLASH_TAG);
        transaction.commit();
    }

    public void initFragmentScreen() {
        isThreadRun = false;
        if (FlashLightWidget.lightOn) {
            OpenWidgetApp(false);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, Screen.newInstance(), Variable.FRAGMENT_SCREEN_TAG);
        transaction.commit();
    }

    public void initFragmenTextSlide() {
        isThreadRun = false;
        if (FlashLightWidget.lightOn) {
            OpenWidgetApp(false);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.contentView, TextSlide.newInstance(), Variable.FRAGMENT_TEXTSLIDE_TAG);
        transaction.commit();
    }

    public void initReference() {

        btnChooseType = (Button) findViewById(R.id.btnChooseType);
        btnChooseType.setTypeface(cFonts.get_RO_BOLD());
        btnOpenFlashPage = (Button) findViewById(R.id.btnOpenFlashPage);
        btnOpenScreenPage = (Button) findViewById(R.id.btnOpenScreenPage);
        lnHeader = (LinearLayout) findViewById(R.id.lnHeader);
        rlSetting_Popup = (RelativeLayout) findViewById(R.id.rlSetting_Popup);
        swDefault = (SwitchButton) findViewById(R.id.swDefault);
        swPushAlarm = (SwitchButton) findViewById(R.id.swPushAlarm);
        swScreenKeep = (SwitchButton) findViewById(R.id.swScreenKeep);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting);
        rlViewPreview = (RelativeLayout) findViewById(R.id.rlViewPreview);
        contentView = (FrameLayout) findViewById(R.id.contentView);
        preview = (FrameLayout) findViewById(R.id.surfaceContain);
    }

    public void initFunction() {
        // Flash is choose, So it will be enable false
        getValueSetting();
    }


    public void getValueSetting() {
        Log.i(Variable.TAG, "getValueSetting");
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        swPushAlarm.setChecked(pre.getBoolean(Variable.PUSH_ALARM, true));
        swScreenKeep.setChecked(pre.getBoolean(Variable.SCREEN_KEEP, false));
        swDefault.setChecked(pre.getBoolean(Variable.DEFAULT_STATE, true));
    }

    //call action by XML layout
    public void activeSaveSetting(View view) {

        if (isFlag) {
            return;
        }
        isFlag = true;
        Log.i(Variable.TAG, "activeSaveSetting");
        view.setEnabled(false);
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(Variable.DEFAULT_STATE, swDefault.isChecked());
        editor.putBoolean(Variable.PUSH_ALARM, swPushAlarm.isChecked());
        editor.putBoolean(Variable.SCREEN_KEEP, swScreenKeep.isChecked());
        editor.commit();
        view.setEnabled(true);
        showDialogSaveSetting();
        isFlag = false;
    }

    public void activeCancelSetting(View view) {
        openOrClosePopupSetting();
    }

    public void activeOpenSettingController(View view) {
        getValueSetting();
        openOrClosePopupSetting();
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (pre.getBoolean(Variable.DEFAULT_STATE, true)) {
            Log.i(Variable.TAG, "Return True");
        } else {
            Log.i(Variable.TAG, "Return Flase");
        }
    }

    public void openOrClosePopupSetting() {
        if (isFlag) {
            return;
        }
        isFlag = true;

        rlSetting_Popup.setVisibility(btnSetting.isSelected() ? View.GONE : View.VISIBLE);
        btnSetting.setSelected(btnSetting.isSelected() ? false : true);
        isFlag = false;
    }

    public void actionChooseTypeApp(View view) {
        if (isFlag) {
            return;
        }
        isFlag = true;


        Button btn = (Button) view;
        String oneOfTwoStatus = getResources().getString(R.string.TOP_FLASHLIGHT);
        boolean isFlash = btn.getText().toString().equalsIgnoreCase(oneOfTwoStatus);
        btn.setText(isFlash ? R.string.TOP_SCREEN : R.string.TOP_FLASHLIGHT);
        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_flash));
        if (isFlash) {
            initFragmentScreen();
        } else {
            initFragmentFlash(false);
        }
        // End function

        isFlag = false;
    }

    public void actionHeaderActiveTextSlide(View view) {
        if (isFlag) {
            return;
        }
        isFlag = true;

        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_textslide));
        btnChooseType.setText(R.string.TOP_SCREEN);
        initFragmenTextSlide();
        isFlag = false;
    }

    public void actionHeaderActiveFlash(View view) {
        if (isFlag) {
            return;
        }
        isFlag = true;
        lnHeader.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_header_active_flash));
        String oneOfTwoStatus = getResources().getString(R.string.TOP_FLASHLIGHT);
        boolean isFlash = btnChooseType.getText().toString().equalsIgnoreCase(oneOfTwoStatus);
        if (isFlash) {
            initFragmentFlash(false);
        } else {
            initFragmentScreen();
        }
        isFlag = false;
    }

    public void actionGoToBabosarangApp(View view) {

        if (!isNetworkAvailable(getApplicationContext())) {
            // code here
            Toast.makeText(getApplicationContext(), "Not Internet Connection!!", Toast.LENGTH_LONG).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("uid", "com.eproject.vn.flashligh");

        Log.i(Variable.TAG, json.toString());

        Ion.with(getApplicationContext())
                .load("http://www.babosarang.co.kr/app_api/app_banner.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result != null) {
                            Log.i(Variable.TAG, result.toString());
                            JsonArray jsonArray = result.getAsJsonArray("date");
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                            String bannerPath = jsonObject.get("banner_path").getAsString();
                            String link = "http://m.babosarang.co.kr" + jsonObject.get("url_link").getAsString();
                            Log.i(Variable.TAG, bannerPath);
                            Log.i(Variable.TAG, link);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        } else {
                            Log.i(Variable.TAG, e.toString());
                        }

                    }
                });


    }

    public void actionClosePreview(View view) {
        rlViewPreview.setVisibility(View.GONE);
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
        Log.i(Variable.TAG, "onStop");
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
                Log.i(Variable.TAG, "Repond FLASH_ON");
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
                Log.i(Variable.TAG, "Repond FLASH_OFF");
                isThreadRun = false;
                break;
            default:
                break;
        }
    }


    public void initCamera() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
//        FrameLayout preview = (FrameLayout) findViewById(R.id.surfaceContain);
//        preview.addView(mPreview);
    }

    public void setDelay(int pDelay) {
        delay = pDelay;
        // error if clear it
        Log.i(Variable.TAG, "Delay in " + String.valueOf(delay));
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
