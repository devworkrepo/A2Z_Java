package com.a2zsuvidhaa.in.util;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

public class DrawableResources {

    public static Drawable setDrawableCustomColor(Context context,int color,int drawable){
        Drawable myIcon = context.getResources().getDrawable(drawable);
        ColorFilter filter = new LightingColorFilter( color,color);
        myIcon.setColorFilter(filter);
        return myIcon;
    }
}
