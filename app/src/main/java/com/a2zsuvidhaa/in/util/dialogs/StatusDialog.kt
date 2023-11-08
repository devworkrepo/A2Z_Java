package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.dialogFullScreenConfiguration
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.set
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.enums.LottieType
import com.airbnb.lottie.LottieAnimationView


object StatusDialog {

    fun progress(context: Context, title: String = "Loading..."): Dialog {
        return dialogConfiguration(context, R.layout.dialog_loading,cancelable = false).apply {
            findViewById<TextView>(R.id.tv_title).text = title
            show()
        }
    }

    fun fullScreenProgress(context: Context
    ): Dialog {
        return dialogFullScreenConfiguration(context, R.layout.dialog_full_screen_progress, cancelable = false).apply { show() }
    }


    fun success(
        activity: Activity,
        message: String = "Success",
        title: String? = null,
        onComplete: () -> Unit = {},
    ) = statusDialog(activity, message, onComplete, StatusType.SUCCESS,title)


    fun failure(
        activity: Activity,
        message: String = "Failure",
        onComplete: () -> Unit = {}
    ) = statusDialog(activity, message, onComplete, StatusType.FAILURE)

    fun pending(
        activity: Activity,
        message: String = "Pending"
    ) = statusDialog(activity, message, StatusType.PENDING)


    fun alert(
            activity: Activity,
            message: String,
            onComplete: () -> Unit = {},
    ) = statusDialog(activity, message,onComplete, StatusType.ALERT)



    fun success(
            activity: Activity,
            message: String = "Success"
    ) = statusDialog(activity, message, StatusType.SUCCESS)


    fun failure(
            activity: Activity,
            message: String = "Failure"
    ) = statusDialog(activity, message, StatusType.FAILURE)

    fun pending(
            activity: Activity,
            message: String = "Pending",
            onComplete: () -> Unit = {}
    ) = statusDialog(activity, message, onComplete, StatusType.PENDING)


    private fun statusDialog(
            context: Context,
            message: String,
            type: StatusType,

    ) = dialogConfiguration(
            context = context,
            layout = R.layout.dialog_status
    ).apply {

        val tvMessage = findViewById<TextView>(R.id.tv_message).also { it.text = message }

        val lottieView = findViewById<LottieAnimationView>(R.id.lottie_view)

        when (type) {

            StatusType.SUCCESS -> {
                lottieView.set(LottieType.SUCCESS)
                tvMessage.setupTextColor(R.color.green)
            }
            StatusType.FAILURE -> {
                lottieView.set(LottieType.FAILURE)
                tvMessage.setupTextColor(R.color.red)
            }
            StatusType.PENDING -> {
                lottieView.set(LottieType.PENDING)
                tvMessage.setupTextColor(R.color.yellow_dark)
            }
            StatusType.ALERT ->{
                lottieView.set(LottieType.ALERT)
                tvMessage.setupTextColor(R.color.colorPrimaryDark)
            }
        }

        findViewById<Button>(R.id.btn_ok).setOnClickListener { dismiss() }
        show()
    }


    private fun statusDialog(
        context: Context,
        message: String,
        onComplete: () -> Unit,
        type: StatusType,
        title : String? = null,
    ) = dialogConfiguration(
        context = context,
        layout = R.layout.dialog_status
    ).apply {

        val tvMessage = findViewById<TextView>(R.id.tv_message).also { it.text = message }
        val lottieView = findViewById<LottieAnimationView>(R.id.lottie_view)
        val tvTitle = findViewById<TextView>(R.id.tv_title).also { it.text = title }
        if(title == null) tvTitle.hide() else tvTitle.show()

        when (type) {

            StatusType.SUCCESS -> {
                lottieView.set(LottieType.SUCCESS)
                tvMessage.setupTextColor(R.color.green)
            }
            StatusType.FAILURE -> {
                lottieView.set(LottieType.FAILURE)
                tvMessage.setupTextColor(R.color.red)
            }
            StatusType.PENDING -> {
                lottieView.set(LottieType.PENDING)
                tvMessage.setupTextColor(R.color.yellow_dark)
            }
            StatusType.ALERT -> {
                lottieView.set(LottieType.ALERT)
                tvMessage.setupTextColor(R.color.grey_700)
            }
        }

        findViewById<Button>(R.id.btn_ok).setOnClickListener { dismiss() }
        setOnDismissListener { onComplete() }
        show()
    }

    enum class StatusType {
        SUCCESS, FAILURE, PENDING,ALERT
    }
}