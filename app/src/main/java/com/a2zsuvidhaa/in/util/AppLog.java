package com.a2zsuvidhaa.in.util;

import android.util.Log;

import com.a2zsuvidhaa.in.BuildConfig;

public class AppLog {
    private static boolean isDebug= BuildConfig.DEBUG;

    public static void d(String message){
        if(isDebug){
            Log.d("AppLog",message);
        }
    }
    public static void d(String TAG,String message){
        if(isDebug){
            Log.d(TAG,message);
        }
    }

    public static void e(String message){
        if(isDebug){
            Log.d("AppLog",message);
        }
    }
    public static void e(String TAG,String message){
        if(isDebug){
            Log.d(TAG,message);
        }
    }
}
