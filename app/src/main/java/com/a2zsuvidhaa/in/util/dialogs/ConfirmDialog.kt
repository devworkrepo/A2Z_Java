package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.*
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.google.android.material.textfield.TextInputLayout

object ConfirmDialog {



    fun dmtTransactionConfirmation(
            context: Context,
            dmtType: DmtType,
            beneficiaryName: String,
            accountNumber: String,
            bankName: String,
            amount: String,
            ifsc: String,
            onConfirm: () -> Unit
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_dmt_transaction_confirmation).apply {

            val tvIfsc = findViewById<TextView>(R.id.tv_ifsc_code)
            val tvBankName = findViewById<TextView>(R.id.tv_bank_name)
            val tvtTitleAccountNumber = findViewById<TextView>(R.id.tv_title_account_number)
            val tvBankNameTitle = findViewById<TextView>(R.id.tv_bank_name_title)

            if (dmtType == DmtType.UPI) {
                findViewById<LinearLayout>(R.id.ll_ifsc).hide()
                tvtTitleAccountNumber.text = "Upi ID"
                tvBankNameTitle.text = "Provider"
            }

            findViewById<TextView>(R.id.tv_name).text = beneficiaryName
            tvBankName.text = bankName
            findViewById<TextView>(R.id.tv_account_number).text = accountNumber
            tvIfsc.text = ifsc
            findViewById<TextView>(R.id.tv_amount).text = "₹ $amount"

            val tilAmount = findViewById<TextInputLayout>(R.id.til_confirm_amount)
            val edAmount = findViewById<EditText>(R.id.ed_confirm_amount)


            ViewUtil.resetErrorOnTextInputLayout(tilAmount)

            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<Button>(R.id.btn_confirm).setOnClickListener {

                val confirmAmount = edAmount.text.toString()

                val isValid = isValid(confirmAmount, amount, tilAmount)

                if(isValid){

                    dismiss()
                    onConfirm()
                }

            }

            show()
        }
    }

    private fun isValid(confirmAmount: String,
                        amount: String,
                        tilAmount: TextInputLayout): Boolean {


        if (confirmAmount == amount) {
            tilAmount.apply {
                this.isErrorEnabled = false
            }
        } else {
            tilAmount.apply {
                this.isErrorEnabled = true
                this.error = "Amount didn't matched"
            }
            return false
        }
        return true
    }


}