package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.CountDownTimer
import android.text.InputFilter
import android.widget.*
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.ents.afterTextChange
import com.a2zsuvidhaa.`in`.util.ents.disable
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import com.google.android.material.textfield.TextInputLayout

class OtpCounterDialog {


    var dialog: Dialog? = null
    private var timer: CountDownTimer? = null
    private var edOTp: EditText? = null
    private var tilOtp : TextInputLayout? = null
    private var btnVerify: Button? = null
    private var btnResend: Button? = null
    private var tvWaitingTime: TextView? = null
    private var progress: ProgressBar? = null
    private var tvCountDown: TextView? = null
    private var otp: String = ""

    fun onResendLoading() {
        progress?.show()
        tvWaitingTime?.hide()
        tvCountDown?.hide()
        btnResend?.hide()
    }

    fun clearOtpEditText() {
        edOTp?.setText("")
        otp = ""
    }

    fun initCountDownTimer() {
        progress?.hide()
        tvWaitingTime?.show()
        tvCountDown?.show()
        btnResend?.hide()
        countDownTime()
    }

    fun onResendFailure() {
        progress?.hide()
        tvWaitingTime?.hide()
        tvCountDown?.hide()
        btnResend?.show()
    }

    private fun countDownTime() {
        val duration = if (BuildConfig.DEBUG) 6000L else 60000L
        timer?.cancel()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerInSecond = (millisUntilFinished / 1000).toInt()
                tvCountDown?.text = timerInSecond.toString()
            }

            override fun onFinish() {
                onResendFailure()
            }
        }
        timer?.start()
    }

    fun onDestroyed() {
        dialog = null
        timer?.cancel()
        timer?.let { timer = null }
    }

    fun showDialog(
            context: Activity,
            onVerifyOtp: (String) -> Unit,
            onResendOtp: () -> Unit,
            otpLength : Int = 4,
            title: String? = null,
            subTitle: String? = null
    ) {
        dialog = dialogConfiguration(context, R.layout.dialog_otp_verify).apply {

            btnVerify = findViewById(R.id.btn_verify)
            btnResend = findViewById(R.id.btn_resend)
            tvWaitingTime = findViewById(R.id.tv_waiting_hint)
            edOTp = findViewById(R.id.ed_otp)
            progress = findViewById(R.id.progress)
            tvCountDown = findViewById(R.id.tv_count_down)
            tilOtp = findViewById(R.id.til_otp)

            edOTp?.filters = arrayOf(InputFilter.LengthFilter(otpLength))

            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }

            btnResend?.setOnClickListener { onResendOtp() }

            btnVerify?.setOnClickListener {
                otp = edOTp!!.text.toString()
                if(otpLength == otp.length){
                    tilOtp?.isErrorEnabled = false
                    onVerifyOtp(otp)
                }else{
                    tilOtp?.error = "Enter $otpLength digits Otp"
                    tilOtp?.isErrorEnabled = true
                }
            }

            setOnDismissListener {
                onDestroyed()
            }

            initCountDownTimer()
            show()
        }
    }
}

