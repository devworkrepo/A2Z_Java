package com.a2zsuvidhaa.in;

import android.app.Application;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.RootTools;

import dagger.hilt.android.HiltAndroidApp;

import static com.a2zsuvidhaa.in.util.AppConstants.BASE_URL;

@HiltAndroidApp
public class MyApplication extends Application {

    public static boolean isRooted;

    public static String currentAppVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        isRooted = RootTools.isDeviceRooted();
        new APIs(BASE_URL, "userId", "token", "password", "ASKJHAU123SHYEWR"
                , getApplicationContext()
                .getResources()
                .getString(R.string.open));
        currentAppVersion = getCurrentAppVersion();


    }

    private String getCurrentAppVersion() {
        String currentVersion = "0";
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersion;
    }
}
