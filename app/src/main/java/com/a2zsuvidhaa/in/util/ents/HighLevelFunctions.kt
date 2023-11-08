package com.a2zsuvidhaa.`in`.util.ents

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.util.CashUtil

inline fun getValueAnimator(
    forward: Boolean = true,
    duration: Long,
    interpolator: TimeInterpolator,
    crossinline updateListener: (progress: Float) -> Unit
): ValueAnimator {
    val a =
        if (forward) ValueAnimator.ofFloat(0f, 1f)
        else ValueAnimator.ofFloat(1f, 0f)
    a.addUpdateListener { updateListener(it.animatedValue as Float) }
    a.duration = duration
    a.interpolator = interpolator
    return a
}

fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio

    val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
    val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
    val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
    val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()
inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

inline val Context.screenWidth: Int
    get() = Point().also {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(
            it
        )
    }.x
inline val View.screenWidth: Int
    get() = context!!.screenWidth

fun View.setScale(scale: Float) {
    this.scaleX = scale
    this.scaleY = scale
}

fun Any.bindColor(context: Context, @ColorRes id: Int) = lazy(LazyThreadSafetyMode.NONE) {
    ContextCompat.getColor(context, id)
}

fun Any.bindDimen(context: Context, @DimenRes id: Int) = lazy(LazyThreadSafetyMode.NONE) {
    context.resources.getDimension(id)
}


fun onAmountChange(edAmount: EditText, tvAmountInWord: TextView, button: Button?=null, balance: String) {

    val strAmount = edAmount.text.toString()
    if (strAmount.isNotEmpty() && strAmount != "0") {
        val amount = strAmount.toDouble()
        tvAmountInWord.text = CashUtil.doubleConvert(amount)
        if (amount in 100.0..25000.0) {
            if (balance.toDouble() >= amount) {
                button?.run {
                    this.alpha = 1.0f
                    this.isEnabled = true
                }
            } else {
                tvAmountInWord.text = "Insufficient user wallet balance!"
                button?.run {
                    this.alpha = 0.5f
                    this.isEnabled = false
                }
            }
        } else {
            if (amount > 25000)
                tvAmountInWord.text =
                    "Can't transfer amount greater than 25000"
            button?.run {
                this.alpha = 0.5f
                this.isEnabled = false
            }
        }
    } else tvAmountInWord.text = "Enter amount"


}