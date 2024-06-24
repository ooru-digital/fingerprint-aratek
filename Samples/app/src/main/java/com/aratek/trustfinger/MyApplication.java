package com.aratek.trustfinger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

//import com.aratek.trustfinger.common.Constants;

import com.dy.io.control.IO;
import com.dy.io.control.IoControl;
import com.dy.io.control.STATUS;

import java.io.File;

/**
 * Created by hecl on 2018/10/10.
 */
public class MyApplication extends Application {
    public boolean isLedEnable() {
        return isLedEnable;
    }

    public void setLedEnable(boolean ledEnable) {
        isLedEnable = ledEnable;
    }

    private boolean isLedEnable = true;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int appCount ;
    private IoControl mIoControl ;
    @Override
    public void onCreate() {
        super.onCreate();
        mIoControl = new IoControl(this);
        initBackgroundCallBack();
        sp = getSharedPreferences(Config.SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        put(Config.AUTO_SAVE, true);
        put(Config.FEATURE_PATH, Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "AratekTrustFinger" + File.separator +
                "FingerData");
        put(Config.FEATURE_FORMAT, "Aratek Bione");
        put(Config.IMAGE_FORMAT, "BMP");
        put(Config.CAPTURE_IMAGE_QUALITY_THRESHOLD, "50");
        put(Config.ENROLL_IMAGE_QUALITY_THRESHOLD, "50");
        put(Config.VERIFY_IMAGE_QUALITY_THRESHOLD, "50");
        put(Config.VERIFY_SECURITY_LEVEL, "Level4");
        put(Config.IDENTIFY_IMAGE_QUALITY_THRESHOLD, "50");
        put(Config.IDENTIFY_SECURITY_LEVEL, "Level4");
        editor.apply();
    }

    public void put(String key, String value) {
        if (!sp.contains(key)) {
            editor.putString(key, value);
        }

    }

    public void put(String key, boolean value) {
        if (!sp.contains(key)) {
            editor.putBoolean(key, value);
        }
    }
    private void initBackgroundCallBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    appCount++;
                    mIoControl.setIo(IO.UFP, STATUS.ON);
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    appCount--;
                    if (appCount == 0) {
                        mIoControl.setIo(IO.UFP, STATUS.OFF);
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }
            });
        }
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }


}
