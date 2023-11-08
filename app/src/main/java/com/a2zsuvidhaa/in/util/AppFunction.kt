package com.a2zsuvidhaa.`in`.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import com.a2zsuvidhaa.`in`.R
import java.util.*

//DIALOG CONFIGURATION HELPER
fun dialogConfiguration(context: Context, layout: Int,cancelable: Boolean  = true,widthPercentage : Double= 0.85) : Dialog {
    val dialog =  Dialog(context)
    dialog.setContentView(layout)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(cancelable)
    val cardView = dialog.findViewById<CardView>(R.id.root_layout)
    val displayMetrics = context.resources.displayMetrics
    val widthLcl = (displayMetrics.widthPixels * widthPercentage).toInt()
    val paramsLcl = cardView.layoutParams as FrameLayout.LayoutParams
    paramsLcl.width = widthLcl
    paramsLcl.gravity = Gravity.CENTER
    cardView.layoutParams = paramsLcl
    Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun dialogFullScreenConfiguration(context: Context,layout: Int,cancelable : Boolean = true) : Dialog{
    val dialog =  Dialog(context,R.style.AppTheme)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(layout)
    dialog.setCancelable(cancelable)
    dialog.setCanceledOnTouchOutside(false)
    return dialog
}



inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}