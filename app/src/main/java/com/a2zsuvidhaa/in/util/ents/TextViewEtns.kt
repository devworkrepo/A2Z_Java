package com.a2zsuvidhaa.`in`.util.ents

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import com.a2zsuvidhaa.`in`.util.enums.DmtType

fun TextView.blink() {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 500 //You can manage the blinking time with this parameter
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    this.startAnimation(anim)
}