package com.a2zsuvidhaa.`in`.util.dialogs

import android.content.Context
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.dialogFullScreenConfiguration
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast


object UpdateDialog {
    fun showUpdate(
        context: Context,
        isForceUpdate: Boolean,
        onUpdate: () -> Unit
    ) {

        context.showToast(isForceUpdate.toString())

        dialogFullScreenConfiguration(
            context,
            R.layout.dialog_full_screen_update,
            cancelable = !isForceUpdate
        ).apply {
            findViewById<TextView>(R.id.tv_message).text =
                if (isForceUpdate) "There are some major changes. To continue spay service need to update app first. Thank-You"
                else "New functionality are added, to take benefit update the app"

            val btnCancel = findViewById<ImageButton>(R.id.btn_cancel)
            if(isForceUpdate) btnCancel.hide()
            else btnCancel.show()

            btnCancel.setOnClickListener{
                dismiss()
            }

            findViewById<Button>(R.id.btn_update).setOnClickListener {
                onUpdate()
            }
            show()
        }
    }
}