@file:JvmName("BindingUtil")

package com.a2zsuvidhaa.`in`.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor

/*@BindingAdapter("setVisibilityFromLiveData")
fun setVisibilityFromLiveData(textView: TextView, data: LiveData<String>) {
    try {
        textView.text = data.value
        if (data.value == "") {
            textView.hide()
            textView.text = ""
        } else {
            textView.show()
        }
    } catch (e: Exception) {
        textView.hide()
    }
}*/

@BindingAdapter("setTransactionStatus")
fun setStatusFromInt(view : View,data : Int){
    if(view is TextView){
        when (data) {
            1 -> view.apply {
                text = "Success"
                setupTextColor(R.color.green)
            }
            2 ->  view.apply {
                text = "Failure"
                setupTextColor(R.color.red)
            }
            3 ->  view.apply {
                text = "Pending"
                setupTextColor(R.color.yellow_dark)
            }
            34 ->  view.apply {
                text = "Accepted"
                setupTextColor(R.color.colorPrimaryDark)
            }

            37 ->  view.apply {
                text = "Initiated"
                setupTextColor(R.color.colorPrimaryDark)
            }
            else ->view.apply {
                text = "Pending"
                setupTextColor(R.color.yellow_dark)
            }
        }

    }
}
