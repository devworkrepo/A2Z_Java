package com.a2zsuvidhaa.`in`.activity.register.user

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.response.RegisterCompleteUser
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor

object RegisterUserInfoDialog {

    private fun getKycStatus(item: String) = when (item) {
        "YES" -> 1
        "NO" -> 2
        "PENDING" -> 3
        else -> 0
    }

    private fun setupTextImageView(
        imageView: ImageView,
        textView: TextView,
        panVerified: String
    ) {
        val (color: Int, imageIcon: Int, data: String) = when (getKycStatus(panVerified)) {
            1 -> Triple(R.color.green, R.drawable.ic_check_tick, "Completed")
            2 -> Triple(R.color.grey_600, R.drawable.ic_baseline_info_24, "InComplete")
            else -> Triple(R.color.colorPrimaryDark, R.drawable.ic_baseline_info_24, "Pending")
        }

        textView.apply {
            text = data
            setupTextColor(color)
        }

        imageView.apply {
            setImageDrawable(ContextCompat.getDrawable(context!!, imageIcon))
            setColorFilter(ContextCompat.getColor(context!!, color))
        }

    }

    operator fun invoke(
        context: Context,
        item: RegisterCompleteUser,
        onKycProceed: (RegisterUserKycType) -> Unit
    ): Dialog {
        return dialogConfiguration(
            context,
            R.layout.dialog_register_user_detail,
            widthPercentage = 0.95
        ).apply {
            findViewById<TextView>(R.id.tv_user_detail).text = item.userDetails
            findViewById<TextView>(R.id.tv_mobile).text = item.mobile
            findViewById<TextView>(R.id.tv_email).text = item.email
            findViewById<TextView>(R.id.tv_shop_name).text = item.shopName
            findViewById<TextView>(R.id.tv_parent).text = item.parentDetails

            //kyc info image view
            val ivPanVerified = findViewById<ImageView>(R.id.iv_pan_verified)
            val ivAadhaarKyc = findViewById<ImageView>(R.id.iv_aadhaar_kyc)
            val ivDocumentKyc = findViewById<ImageView>(R.id.iv_document_kyc)
            val ivAepsKyc = findViewById<ImageView>(R.id.iv_aeps_kyc)

            //kyc info text view
            val tvPanVerified = findViewById<TextView>(R.id.tv_pan_verified)
            val tvAadhaarKyc = findViewById<TextView>(R.id.tv_aadhaar_kyc)
            val tvDocumentKyc = findViewById<TextView>(R.id.tv_document_kyc)
            val tvAepsKyc = findViewById<TextView>(R.id.tv_aeps_kyc)


            setupTextImageView(ivPanVerified, tvPanVerified, item.panVerified)
            setupTextImageView(ivAadhaarKyc, tvAadhaarKyc, item.aadhaarKyc)
            setupTextImageView(ivDocumentKyc, tvDocumentKyc, item.documentKyc)
            setupTextImageView(ivAepsKyc, tvAepsKyc, item.aepsKyc)


            val button = findViewById<Button>(R.id.btn_proceed)
            val isKycInComplete = item.panVerified == "NO" ||
                    item.aadhaarKyc == "NO" ||
                    item.documentKyc == "NO" ||
                    item.aepsKyc == "NO"

            if (isKycInComplete) button.text = "Complete Kyc"
            else button.text = "Done"


            button.setOnClickListener {
                if (isKycInComplete) {
                    if (item.panVerified == "NO")
                        onKycProceed(RegisterUserKycType.PAN)
                    else if (item.aadhaarKyc == "NO")
                        onKycProceed(RegisterUserKycType.AADHAAR)
                    else if (item.documentKyc == "NO")
                        onKycProceed(RegisterUserKycType.DOCUMENT)
                    else if (item.aepsKyc == "NO")
                        onKycProceed(RegisterUserKycType.AEPS)
                }
                dismiss()
            }


            show()

        }
    }


}

enum class RegisterUserKycType {
    PAN, AADHAAR, DOCUMENT, AEPS
}
