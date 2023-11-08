package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Activity
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.DateUtil
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setup
import com.a2zsuvidhaa.`in`.util.ents.show
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import java.util.LinkedHashMap

class LedgerFilterDialog(
        private var status: String,
        private var fromDate: String,
        private var toDate: String,
        private var product: String,
        private var criteria: String,
        private var inputNumber: String
) {


    private var llSearch: LinearLayout? = null
    private var atStatus: AutoCompleteTextView? = null
    private var atProduct: AutoCompleteTextView? = null
    private var atCriteria: AutoCompleteTextView? = null

    private var tilCriteria: TextInputLayout? = null
    private var tilSearchInputText: TextInputLayout? = null

    private var edInputSearchText: EditText? = null


    fun showDialog(
            activity: Activity,
            onSearchClick: (
                    fromDate: String,
                    toDate: String,
                    selectedProduct: String,
                    selectedStatus: String,
                    selectedCriteria: String,
                    transactionNumber: String,
            ) -> Unit
    ) = setupDialog(activity).apply {


        //init views
        llSearch = findViewById(R.id.ll_search)
        atStatus = findViewById(R.id.actv_status)
        atProduct = findViewById(R.id.actv_product)
        atCriteria = findViewById(R.id.actv_criteria)
        tilCriteria = findViewById(R.id.til_actv_criteria)
        tilCriteria?.hide()
        tilSearchInputText = findViewById(R.id.til_input_search_text)
        tilSearchInputText?.hide()
        edInputSearchText = findViewById(R.id.ed_input_search_text)


        setupInitialForCriteriaAndNumber()



        setupProductList()

        setupStatusList()

        llSearch?.setOnClickListener {
            inputNumber = edInputSearchText?.text.toString()
            dismiss()
            onSearchClick(fromDate, toDate, product, status, criteria, inputNumber)

        }

    }

    private fun setupInitialForCriteriaAndNumber() {

        if (product.isEmpty()) return
        val productTitle = productList.entries.find { it.value == product }?.key!!
        val mCriteriaList = criteriaList(productTitle)


        if(criteria.isEmpty()){
            tilSearchInputText?.hide()
            inputNumber = ""
        }
        else{

            AppLog.d("criteria : $criteria")
            mCriteriaList.entries.find { it.value == criteria }?.key?.let {
                atCriteria?.setText(it)
                edInputSearchText?.setText(inputNumber)
                tilSearchInputText?.hint = it
                tilSearchInputText?.show()
            }
        }
        setupCriteriaList(mCriteriaList)




    }


    private fun setupProductList() {

        if (product.isNotEmpty()) {
            productList.entries.find { it.value == product }?.key?.let {  atProduct?.setText(it) }
        }

        val mList = productList.keys.toList() as ArrayList<String>
        atProduct?.setup(mList) {
            val key = if (it == "All") "" else it
            product = if (key.isNotEmpty()) productList[it].orEmpty() else ""

            if (product.isNotEmpty()) {
                tilSearchInputText?.hide()
                inputNumber = ""
                atCriteria?.text = null
                val mCriteriaList = criteriaList(it)
                setupCriteriaList(mCriteriaList)
            } else {
                tilCriteria?.hide()
                inputNumber = ""
                tilSearchInputText?.hide()

            }
        }

    }

    private fun setupStatusList() {

        if (status.isNotEmpty()) {
            statusList.entries.find { it.value == status }?.key?.let { atStatus?.setText(it) }
        }


        val mList = statusList.keys.toList() as ArrayList<String>
        atStatus?.setup(mList) {
            val key = if (it == "All") "" else it
            status = if (key.isNotEmpty()) statusList[it].orEmpty() else ""
        }
    }

    private fun setupCriteriaList(criteriaList: LinkedHashMap<String, String>) {
        tilCriteria?.show()
        val mList = criteriaList.keys.toList() as ArrayList<String>
        atCriteria?.setup(mList) {criteriaMap->
             criteriaList[criteriaMap]?.let {criteriaValue ->
                 criteria  = criteriaValue
                 inputNumber = ""
                 tilSearchInputText?.hint = criteriaList.entries.find { it.value == criteriaValue }?.key
                 tilSearchInputText?.show()

             }
        }
    }


    private fun setupDialog(
            activity: Activity,
    ): BottomSheetDialog {


        //init dialog
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter_dialog)

        //init view
        val edFromDate = bottomSheetDialog.findViewById<EditText>(R.id.ed_from_date)
        val edToDate = bottomSheetDialog.findViewById<EditText>(R.id.ed_to_date)


        edFromDate?.setText(fromDate)
        edToDate?.setText(toDate)
        //onClickListener
        edFromDate?.setOnClickListener {
            DateUtil.datePicker(activity, disableFutureDate = true) {
                fromDate = it
                edFromDate.setText(it)
            }
        }
        edToDate?.setOnClickListener {
            DateUtil.datePicker(activity, disableFutureDate = true) {
                toDate = it
                edToDate.setText(it)
            }
        }

        bottomSheetDialog.show()
        return bottomSheetDialog
    }


    private val statusList = linkedMapOf(
            "All" to "",
            "Accepted" to "34",
            "Success" to "1",
            "Failure" to "2",
            "Pending" to "3",
            "Refunded" to "4",
            "Successful" to "24",
            "Refund Success" to "21",
            "Debit" to "6",
            "Credit" to "7",
    )

    private val productList = linkedMapOf(
            "All" to "",
            "Recharge" to "1",
            "A2Z Plus Wallet" to "25",
            "A2Z Plus Two" to "52",
            "A2Z Plus Three" to "53",
            "Acc Verification" to "2",
            "UPI Verification" to "63",
            "VPA Payment" to "62",
            "DMT One" to "50",
            "DMT Two" to "16",
            "BBPS One" to "15",
            "BBPS Two" to "40",
            "Bank Settlement" to "29",
            "Payment Gateway" to "34",
            "AEPS" to "10",
            "Aadhaar Pay" to "28",
            "Reload Card" to "57",
            "Travels" to "100",
            "M-ATM" to "92",
            "M-POS" to "93",
    )

    private fun criteriaList(value: String) = when (value) {
        "Recharge" -> linkedMapOf(
                "Number" to "NUMBER",
                "Order ID" to "ID",
        )
        "A2Z Plus Wallet",
        "A2Z Plus Two",
        "A2Z Plus Three",
        "DMT One",
        "DMT Two",
        "Acc Verification",
        -> linkedMapOf(
                "Account Number" to "NUMBER",
                "Remitter Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )

        "UPI Verification",
        "VPA Payment",
        -> linkedMapOf(
                "UPI ID" to "NUMBER",
                "Remitter Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )

        "BBPS One", "BBPS Two" -> linkedMapOf(
                "Number" to "NUMBER",
                "Customer Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )
        "Aadhaar Pay", "AEPS" -> linkedMapOf(
                "Aadhaar Number" to "NUMBER",
                "Customer Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )
        "Reload Card" -> linkedMapOf(
                "Card Ref Number" to "NUMBER",
                "Mobile Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )
        "Payment Gateway" -> linkedMapOf(
                "Customer Number" to "CUST_MOBILE",
                "Order ID" to "ID",
        )
        "Bank Settlement" -> linkedMapOf(
                "Order ID" to "ID",
                "Account Number" to "NUMBER",
        )

        "Travels" -> linkedMapOf(
                "Order ID" to "ID",
                "PNR Number" to "NUMBER",
        )
        "M-POS","M-ATM" -> linkedMapOf(
            "Card Number" to "NUMBER",
            "Customer Mobile" to "CUST_MOBILE",
            "Order ID" to "ORDERID",
        )
        else -> linkedMapOf()


    }

}


