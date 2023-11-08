package com.a2zsuvidhaa.`in`.util.ents

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.activity.ExceptionActivity
import com.a2zsuvidhaa.`in`.util.AppConstants
import java.io.Serializable

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, title: String,subtitle: String?=null){

    toolbar.title = title
    subtitle?.let { toolbar.subtitle = it }
    setSupportActionBar(toolbar)
    supportActionBar?.let {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
    toolbar.setNavigationOnClickListener { onBackPressed() }
}


fun Fragment.setupToolbar(toolbar: Toolbar, title: String,subtitle : String? = null){

    toolbar.title = title
   subtitle?.let {  toolbar.subtitle = it }
    (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
    (activity as AppCompatActivity?)?. supportActionBar?.let {
        it.setDisplayHomeAsUpEnabled(true)
        it.setDisplayShowHomeEnabled(true)
    }
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
}

fun Context.handleNetworkFailure(e: Exception, shouldBack: Boolean = true) {
    launchIntent(
            ExceptionActivity::class.java, bundleOf(
            AppConstants.EXCEPTION to e as Serializable,
            AppConstants.ABLE_TO_BACK to shouldBack
    )
    )
}


fun Activity?.hideKeyboard() {
    val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = this.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}

