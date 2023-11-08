package com.a2zsuvidhaa.`in`.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.util.ents.disable
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show

open class BaseFragment<B : ViewDataBinding>(private val layout: Int) : Fragment() {

    var progressDialog : Dialog? = null

    private var _binding: B? = null
    val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return _binding?.root
    }

}

open class BaseOtpResendFragment<B : ViewDataBinding>(private val layout: Int) :
    BaseFragment<B>(layout) {

    var timer: CountDownTimer? = null


    fun countDownTime(btnResend: Button, tvTicker: TextView, tvWaitingHint: TextView) {
        val duration = if (BuildConfig.DEBUG) 6000L else 60000L
        timer?.cancel()
        btnResend.disable()
        tvWaitingHint.show()
        tvTicker.show()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerInSecond = (millisUntilFinished / 1000).toInt()
                tvTicker.text = timerInSecond.toString()
            }

            override fun onFinish() {
                btnResend.disable(false)
                tvWaitingHint.hide()
                tvTicker.hide()
            }
        }
        timer?.start()

    }


    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

}