package com.a2zsuvidhaa.in.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.a2zsuvidhaa.in.R;
import java.util.Objects;

public class AppProgressBar {

    private ProgressDialog dialog;

    public static   void enableProgressBar(ProgressDialog dialog, Context context){
      if(dialog != null) {
          Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new
                  ColorDrawable(android.graphics.Color.TRANSPARENT));
          dialog.setIndeterminate(true);
          dialog.setCancelable(false);
          dialog.show();
          dialog.setContentView(R.layout.my_progress);
      }
    }
    public static   void closeProgressBar(ProgressDialog dialog){
        if(dialog!=null) {
            dialog.hide();
        }
    }
}
