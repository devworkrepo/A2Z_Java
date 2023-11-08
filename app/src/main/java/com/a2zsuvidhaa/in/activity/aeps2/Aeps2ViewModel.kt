package com.a2zsuvidhaa.`in`.activity.aeps2

import androidx.lifecycle.LiveData
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.activity.login.globalLocation
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.databinding.ActivityAeps2Binding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import org.json.JSONObject

class Aeps2ViewModel : BaseViewModel() {

    var deviceName : String = ""
    var customerMobileNumber : String=""
    var aadhaarNumber : String=""
    var amount : String=""
    var bankName : String=""
    var bankCode : String=""
    var pidData = ""
    var aepsType=""
    var location = globalLocation
    var transactionType  = TransactionType.CASH_WITHDRAWAL


    lateinit var checkStatusInitialResponse: JSONObject

    fun getTransactionTypeData() = when(transactionType){
        TransactionType.BALANCE_ENQUIRY -> "BE"
        TransactionType.MINI_STATEMENT -> "MS"
        TransactionType.CASH_WITHDRAWAL -> "CW"
        TransactionType.AADHAAR_PAY -> "AADHAAR_PAY"
    }


    fun getTransactionTypeDataFullForm() = when(transactionType){
        TransactionType.BALANCE_ENQUIRY -> "Balance Enquiry"
        TransactionType.MINI_STATEMENT -> "Mini Statement"
        TransactionType.CASH_WITHDRAWAL -> "Cash Withdrawal"
        TransactionType.AADHAAR_PAY -> "Aadhaar Pay"
    }


    fun validateInput(binding : ActivityAeps2Binding): Boolean {
        var isValid = true

        amount = binding.edAmount.text.toString()
        customerMobileNumber = binding.edCustomerMobile.text.toString()
        aadhaarNumber = binding.edAadharNumber.text.toString()


        if (amount.isEmpty()) amount = "0"


        if (bankName.isEmpty()) {
            isValid = false
            binding.tilBankName.apply {
                error = "Select Bank"
                isErrorEnabled = true
            }
        } else binding.tilBankName.isErrorEnabled = false

        if (customerMobileNumber.length != 10) {
            isValid = false
            binding.tilCustomerMobile.apply {
                error = "Enter 10 digit customer mobile number"
                isErrorEnabled = true
            }
        } else binding.tilCustomerMobile.isErrorEnabled = false


        if(aadhaarNumber.length == 12 && !aadhaarNumber.contains("-")){
            if (aadhaarNumber.length != 12) {
                isValid = false
                binding.tilAadharNumber.apply {
                    error = "Enter 12 digit aadhaar number"
                    isErrorEnabled = true
                }
            } else binding.tilAadharNumber.isErrorEnabled = false
        }

        else{
            if (aadhaarNumber.length != 14) {
                isValid = false
                binding.tilAadharNumber.apply {
                    error = "Enter 12 digit aadhaar number"
                    isErrorEnabled = true
                }
            } else binding.tilAadharNumber.isErrorEnabled = false
        }
        




        if (amount.toDouble() !in 100.0..10000.0 &&
                TransactionType.CASH_WITHDRAWAL == transactionType) {
            isValid = false
            binding.tilAmount.apply {
                error = "Enter amount  100 to 10000 Rs."
                isErrorEnabled = true
            }
        } else binding.tilAmount.isErrorEnabled = false

        if(aadhaarNumber.contains("-"))
            aadhaarNumber = aadhaarNumber.replace("-","")

        return isValid

    }

    enum class TransactionType{
        CASH_WITHDRAWAL, MINI_STATEMENT, BALANCE_ENQUIRY,AADHAAR_PAY
    }
}