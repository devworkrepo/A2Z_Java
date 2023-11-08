package com.a2zsuvidhaa.`in`.util

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.LottieType
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout


object ViewUtil {
    fun maskAadhaarNumber(editText: EditText) {

        var str = ""
        var strOldlen = 0

        editText.onTextChange {
            str = editText.text.toString()
            val strLen = str.length
            if (strOldlen < strLen) {
                if (strLen > 0) {
                    if (strLen == 4 || strLen == 9) {
                        str = "$str-"
                        editText.setText(str)
                        editText.setSelection(editText.text!!.length)
                    } else {
                        if (strLen == 5) {
                            if (!str.contains("-")) {
                                var tempStr = str.substring(0, strLen - 1)
                                tempStr += "-" + str.substring(strLen - 1, strLen)
                                editText.setText(tempStr)
                                editText.setSelection(editText.text!!.length)
                            }
                        }
                        if (strLen == 10) {
                            if (str.lastIndexOf("-") != 9) {
                                var tempStr = str.substring(0, strLen - 1)
                                tempStr += "-" + str.substring(strLen - 1, strLen)
                                editText.setText(tempStr)
                                editText.setSelection(editText.text!!.length)
                            }
                        }
                        strOldlen = strLen
                    }
                } else {
                }
            } else {
                strOldlen = strLen
                Log.i(
                    "MainActivity ",
                    "keyDel is Pressed ::: strLen : $strLen\n old Str Len : $strOldlen"
                )
            }
        }
    }

    fun resetErrorOnTextInputLayout(vararg args: TextInputLayout) {
        for (textInputLayout in args) {
            val editText = textInputLayout.editText
            editText?.afterTextChange {
                if (textInputLayout.error != null) {
                    textInputLayout.error = null
                    textInputLayout.isErrorEnabled = false
                }
            }
        }
    }

    fun setupStatus(code: Int, lottieView: LottieAnimationView, textView: TextView) {
        when (code) {
            1 -> {

                lottieView.set(LottieType.SUCCESS)
                textView.setupTextColor(R.color.green)

            }
            2 -> {

                lottieView.set(LottieType.FAILURE)
                textView.setupTextColor(R.color.red)

            }
            else -> {
                lottieView.set(LottieType.PENDING)
                textView.setupTextColor(R.color.yellow_dark)

            }
        }
    }

    fun getStatusBarColorByStatusCode(code: Int, context: Context): Int {
        val color = when (code) {
            1 -> R.color.green
            2 -> R.color.red
            else -> R.color.yellow_dark
        }
        return ContextCompat.getColor(context, color)
    }


    fun extendFab(recyclerView: RecyclerView,fabFilter : ExtendedFloatingActionButton){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Scroll down
                if (dy > 20 && fabFilter.isExtended) {
                    fabFilter.shrink()
                }
                // Scroll up
                if (dy < -20 && !fabFilter.isExtended) {
                    fabFilter.extend()
                }
                // At the top
                if (!recyclerView.canScrollVertically(-1)) {
                    fabFilter.extend()
                }
            }
        })
    }


    fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        editText.postDelayed({
            val keyboard = editText.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            keyboard!!.showSoftInput(editText, 0)
        }, 200)
    }

    fun showHideView(value : Boolean,vararg views : View){

        for (view in views){
            if(value) view.show()
            else view.hide()
        }
    }

    fun ImageButton.setImageIconColor (color : Int){
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled),)
        val colors = intArrayOf(color)
        val myList = ColorStateList(states, colors)
        this.imageTintList = myList
    }


}