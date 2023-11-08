package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.InputFilter
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.BankDownBank
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.dialogFullScreenConfiguration
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupPicassoImage
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout


object Dialogs {

    inline fun fileAndImageChooser(
        context: Context,
        crossinline onItemClick: (
            isFile: Boolean,
            isGallery: Boolean,
            isCamera: Boolean
        ) -> Unit
    ) {
        AlertDialog.Builder(context).apply {
            setTitle("Choose File or Image")
            setIcon(R.drawable.ic_baseline_file_copy_24)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setItems(arrayOf("File", "Gallery", "Camera")) { _, which ->
                when (which) {
                    0 -> onItemClick(true, false, false)
                    1 -> onItemClick(false, true, false)
                    2 -> onItemClick(false, false, true)
                }
            }
            show()
        }
    }

    inline fun imageAndCameraChooser(
        context: Context,
        crossinline onGallery: () -> Unit,
        crossinline onCamera: () -> Unit
    ) {
        AlertDialog.Builder(context).apply {
            setTitle("Choose Camera or Gallery")
            setIcon(R.drawable.ic_baseline_file_copy_24)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setItems(arrayOf("Gallery", "Camera")) { _, which ->
                when (which) {
                    0 -> onGallery()
                    1 -> onCamera()

                }
            }
            show()
        }
    }


    fun confirmField(context: Context, input: String, forInput: String = "Number"): Dialog {
        return dialogConfiguration(context, R.layout.dialog_confirm).apply {
            findViewById<TextView>(R.id.tv_message).text = input
            findViewById<TextView>(R.id.tv_message_1).text = "Please confirm provided $forInput"
            findViewById<TextView>(R.id.tv_message_2).text =
                "$forInput once submitted can not be change"
            findViewById<Button>(R.id.btn_cancel).setOnClickListener { dismiss() }
            show()
        }
    }

    fun confirmBBPSPayment(
        context: Context,
        number: String,
        numberTitle: String,
        amount: String,
        operator: String,
        bbpsType: String,
        commission: String,
        convenienceFee: String
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_confirm_bbps_payment).apply {
            findViewById<TextView>(R.id.tv_commission).text = commission
            findViewById<TextView>(R.id.tv_convenienceFee).text = convenienceFee
            findViewById<TextView>(R.id.tv_bbps_type).text = bbpsType
            findViewById<TextView>(R.id.tv_number).text = number
            findViewById<TextView>(R.id.tv_amount).text = amount
            findViewById<TextView>(R.id.tv_numberTitle).text =
                if (numberTitle.isNotEmpty()) numberTitle else "Number"
            findViewById<TextView>(R.id.tv_operator).text = operator
            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.btn_confirm).text = "Confirm & Pay  (₹$amount)"


            if (commission.equals("0", ignoreCase = true))
                findViewById<LinearLayout>(R.id.ll_commission).hide()
            if (convenienceFee.equals("0", ignoreCase = true))
                findViewById<LinearLayout>(R.id.ll_convenienceFee).hide()
            show()
        }
    }


    fun confirmRecharge(
        context: Context,
        operator: String,
        number: String,
        amount: String
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_confirm_recharge).apply {

            findViewById<TextView>(R.id.tv_operator).text = operator
            findViewById<TextView>(R.id.tv_number).text = number
            findViewById<TextView>(R.id.tv_amount).text = amount
            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.btn_confirm).text = "Confirm And Recharge (₹$amount)"
            show()
        }
    }


    fun rOfferListDialog(
        context: Context
    ): Dialog {
        return dialogFullScreenConfiguration(context, R.layout.dialog_offer_list).apply {
            findViewById<ImageButton>(R.id.btn_close).setOnClickListener {
                dismiss()
            }
            show()
        }
    }


    fun otpVerify(
        context: Context,
        showTimer: Boolean = true,
        otpLength: Int = 4,
        onSubmit: (String) -> Unit = {}
    ): Dialog {
        if (!showTimer) {
            return dialogConfiguration(context, R.layout.dialog_otp_verify).apply {
                findViewById<RelativeLayout>(R.id.rv_count).hide()
                findViewById<TextView>(R.id.tv_waiting_hint).hide()


                val tilOtp = findViewById<TextInputLayout>(R.id.til_otp)
                val edOtp = findViewById<EditText>(R.id.ed_otp)
                edOtp.filters = arrayOf(InputFilter.LengthFilter(otpLength))

                ViewUtil.resetErrorOnTextInputLayout(tilOtp)

                findViewById<Button>(R.id.btn_verify).setOnClickListener {
                    val otp = edOtp.text.toString()
                    if (otp.length == otpLength) {
                        tilOtp.isErrorEnabled = false
                        dismiss()
                        onSubmit(otp)

                    } else {
                        tilOtp.error = "Enter $otpLength digits otp"
                        tilOtp.isErrorEnabled = true

                    }
                }

                findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                    dismiss()
                }
                show()
            }
        } else return dialogConfiguration(context, R.layout.dialog_otp_verify).apply {
            show()
        }
    }

    fun dmtCommissionAmount(context: Context): Dialog {
        return dialogConfiguration(context, R.layout.dialog_dmt_comission_amount).apply {
            show()
        }
    }


    fun commonConfirmDialog(
        context: Context,
        message: String
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_common_confirm).apply {

            findViewById<TextView>(R.id.tv_message).text = message

            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun createLoginID(
        context: Context,
        onProceed: () -> Unit
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_login_id).apply {
            findViewById<ImageButton>(R.id.btn_close).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.btn_create_login_id).setOnClickListener {
                dismiss()
                onProceed()
            }
            show()
        }
    }


    fun logData(
        context: Context,
        data: String,
        onDismiss: () -> Unit
    ): Dialog {
        return dialogFullScreenConfiguration(context, R.layout.dialog_log_data).apply {

            val tvData = findViewById<TextView>(R.id.tv_log_data)
            tvData.text = data

            findViewById<Button>(R.id.btn_back).setOnClickListener {
                dismiss()
            }
            setOnDismissListener {
                onDismiss()
            }
            show()
        }
    }

    fun bluetoothNotPaired(
        context: Context,
        onDismiss: () -> Unit
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_bluetooth_not_paired).apply {
            findViewById<Button>(R.id.btn_go_to_setting).setOnClickListener {
                dismiss()
            }
            setOnDismissListener { onDismiss() }
            show()
        }
    }

    fun docImageView(
        context: Context,
        strImage: String
    ): Dialog {
        return dialogFullScreenConfiguration(context, R.layout.dialog_doc_image).apply {

            val progress = findViewById<ProgressBar>(R.id.progressBar1)
            val imageView = findViewById<ImageView>(R.id.image_view)

            findViewById<Button>(R.id.btn_back).setOnClickListener { dismiss() }
            imageView.setupPicassoImage(strImage) {
                progress.hide()
            }
            show()
        }
    }

    fun docImageView(
        context: Context,
        bitmap: Bitmap,
    ): Dialog {
        return dialogFullScreenConfiguration(context, R.layout.dialog_doc_image).apply {

            val progress = findViewById<ProgressBar>(R.id.progressBar1)
            progress.hide()
            val imageView = findViewById<ImageView>(R.id.image_view)

            findViewById<Button>(R.id.btn_back).setOnClickListener { dismiss() }
            imageView.setImageBitmap(bitmap)
            show()
        }
    }


    fun news(
        context: Context,
        strNews: String
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_news).apply {


            findViewById<ImageButton>(R.id.btn_dismiss).setOnClickListener { dismiss() }

            findViewById<TextView>(R.id.tv_news).text = strNews

            show()
        }
    }

    fun bankDown(
        context: Context,
        data: List<BankDownBank>
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_bank_down_list).apply {


            findViewById<ImageButton>(R.id.btn_dismiss).setOnClickListener { dismiss() }

            val tvBankDown = findViewById<TextView>(R.id.tv_bank_down)

            val bankDownBuilder = StringBuilder()
            data.forEachIndexed { index, value ->

                bankDownBuilder.append("${index + 1} " + value.bankName + "  -  " + value.updatedAt + "\n\n")
            }

            tvBankDown.text = bankDownBuilder.toString()


            show()
        }
    }

    fun selectWallet(
        activity: Activity,
        limitOne: String,
        limitTwo: String,
        limitThree: String,
        onSelect: (dmtType: DmtType) -> Unit
    ): BottomSheetDialog {


        //init dialog
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_wallet_dialog)

        bottomSheetDialog.apply {
            findViewById<TextView>(R.id.tv_limit_one)?.text = "₹ $limitOne"
            findViewById<TextView>(R.id.tv_limit_two)?.text = "₹ $limitTwo"
            findViewById<TextView>(R.id.tv_limit_three)?.text = "₹ $limitThree"
            findViewById<CardView>(R.id.cv_wallet_one)?.setOnClickListener {
                onSelect(DmtType.WALLET_ONE)
                dismiss()
            }
            findViewById<CardView>(R.id.cv_wallet_two)?.setOnClickListener {
                onSelect(DmtType.WALLET_TWO)
                dismiss()
            }
            findViewById<CardView>(R.id.cv_wallet_three)?.setOnClickListener {
                onSelect(DmtType.WALLET_THREE)
                dismiss()
            }
        }

        bottomSheetDialog.show()
        return bottomSheetDialog
    }


}