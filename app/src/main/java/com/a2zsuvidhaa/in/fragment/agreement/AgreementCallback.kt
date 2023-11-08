package com.a2zsuvidhaa.`in`.fragment.agreement

import android.content.Context
import android.webkit.JavascriptInterface
import com.a2zsuvidhaa.`in`.util.ents.showToast

class AgreementCallback (private val callback : (Boolean)->Unit){
    @JavascriptInterface
    fun onAgree(isCheck: String) {
        callback(isCheck.toBoolean())
    }
}