package com.a2zsuvidhaa.`in`.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.a2zsuvidhaa.`in`.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCommissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_commission)
    }
}