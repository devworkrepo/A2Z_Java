package com.a2zsuvidhaa.`in`.util.ents

import android.R.attr.textColor
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.activity.login.LoginActivity
import com.google.android.material.snackbar.Snackbar


fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.gotoLoginActivity(){
    Intent(this, LoginActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(this)
    }
}


fun Context.goToMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    this.startActivity(intent)
}


fun <T : Activity> Context.launchIntent(launchClass: Class<T>, data: Bundle? = null) {
    Intent(this, launchClass).apply {
        data?.let { this.putExtras(data) }
        this@launchIntent.startActivity(this)
    }
}



