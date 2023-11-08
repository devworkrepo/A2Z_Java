package com.a2zsuvidhaa.in.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MakeToast {

    public static void show(Context context,String message){
        if(message==null) message="";
        if(context!=null){

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


        }

    }

    public static void showCenter(Context context,String message)
    {
        Toast toast= Toast.makeText(context, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER,0,0);

        toast.show();
    }
    public static void show(Context context,String message,int length){
        if(message==null) message="";
        if(length>1){
            length =1;
        }
        else if(length<0){
            length =1;
        }
        if(context!=null) {
            Toast.makeText(context, message, length).show();
        }
    }

}
