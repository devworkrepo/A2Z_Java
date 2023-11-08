package com.a2zsuvidhaa.in.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoLogoutManager {

    private static Timer timer;
    public static boolean isSessionTimeout =false;
    public static void startUserSession(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { isSessionTimeout = true; }},60000*(60*2));

    }

    public static void cancelTimer(){
        if(timer!=null ){
            timer.cancel();
            timer = null;
        }
    }


    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void logout(Context context) {

        AppPreference.getInstance(context).delete_id();
        AppPreference.getInstance(context).deleteEmail();
        AppPreference.getInstance(context).deleteOtp();
        AppPreference.getInstance(context).deletToken();
        AppPreference.getInstance(context).deleteUserBalance();
        AppPreference.getInstance(context).deleteRollId();
        AppPreference.getInstance(context).deleteName();
        AppPreference.getInstance(context).deleteAutoLogin();
        AppPreference.getInstance(context).deleteLoginPassword();

        isSessionTimeout=false;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
