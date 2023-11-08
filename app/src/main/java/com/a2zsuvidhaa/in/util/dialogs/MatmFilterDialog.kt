package com.a2zsuvidhaa.`in`.util.dialogs



import android.app.Activity
import android.text.InputFilter
import android.text.InputType
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.DateUtil
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setup
import com.a2zsuvidhaa.`in`.util.ents.show
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout

class MatmFilterDialog(
    private var fromDate: String,
    private var toDate: String,
    private var status: String,
    private var inputMode: String,
    private var transactionType: String,
    private var inputText: String,
) {


    private var atStatus: AutoCompleteTextView? = null
    private var atInputMode: AutoCompleteTextView? = null
    private var atTransactionType: AutoCompleteTextView? = null
    private var tilInput: TextInputLayout? = null
    private var edInput: EditText? = null


    fun showDialog(
        activity: Activity,
        onSearchClick: (
            fromDate: String,
            toDate: String,
            status : String,
            inputMode : String,
            transactionType : String,
            inputText : String
        ) -> Unit
    ) = setupDialog(activity).apply {


        atStatus = findViewById(R.id.actv_transaction_status)
        atInputMode = findViewById(R.id.actv_input_mode)
        atTransactionType = findViewById(R.id.actv_transaction_type)
        tilInput = findViewById(R.id.til_input)
        edInput = findViewById(R.id.ed_input)

        setupStatus()
        setupInputMode()
        setupTransactionType()


        findViewById<LinearLayout>(R.id.ll_search)?.setOnClickListener{

            inputText = edInput?.text.toString()
            dismiss()
            onSearchClick(fromDate,toDate,status,inputMode,transactionType,inputText)
        }


    }

    private fun setupStatus() {

        val mList = transactionStatusList()
        if (status.isNotEmpty()) {
            mList.entries.find { it.value == status }?.key?.let { atStatus?.setText(it) }
        }

        atStatus?.setup(mList.keys.toList() as ArrayList<String>) {
            val key = if (it == "All") "" else it
            status = if (key.isNotEmpty()) mList[it].orEmpty() else ""

        }
    }

    private fun setupInputMode() {
        val mList = inputModeList()
        if (inputMode.isNotEmpty()) {
            mList.entries.find { it.value == inputMode }?.key?.let {
                atInputMode?.setText(it)
                edInput?.text = null
                tilInput?.show()
                if(inputText.isNotEmpty()){
                    edInput?.setText(inputText)
                }
            }
        }

        atInputMode?.setup(mList.keys.toList() as ArrayList<String>) {
            val key = if (it == "None") "" else it
            inputMode = if (key.isNotEmpty()) mList[it].orEmpty() else ""

            onInputModeChange()
        }

    }

    private fun setupTransactionType() {
        val mList = transactionTypeList()
        if (transactionType.isNotEmpty()) {
            mList.entries.find { it.value == transactionType }?.key?.let { atTransactionType?.setText(it) }
        }

        atTransactionType?.setup(mList.keys.toList() as ArrayList<String>) {
            val key = if (it == "All") "" else it
            transactionType = if (key.isNotEmpty()) mList[it].orEmpty() else ""
        }

    }


    private fun onInputModeChange(){
        edInput?.text = null
        tilInput?.hide()
        when(inputMode){
            "NUMBER","ORDERID"->{
                tilInput?.show()
                edInput?.inputType = InputType.TYPE_CLASS_TEXT
                edInput?.filters = arrayOf(InputFilter.LengthFilter(20))
            }
            "CUST_MOBILE"->{
                tilInput?.show()
                edInput?.inputType = InputType.TYPE_CLASS_NUMBER
                edInput?.filters = arrayOf(InputFilter.LengthFilter(10))
            }
        }
    }

    private fun setupDialog(
        activity: Activity,
    ): BottomSheetDialog {


        //init dialog
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_money_report)

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


    private fun transactionTypeList() = linkedMapOf(
            "All" to "",
            "Balance Enquiry" to "MATM(BALANCE_INQUIRY)",
            "Cash Withdrawal" to "MATM(CASH_WITHDRAWAL)",
            "Mpos / Sale" to "SALE",
    )

    private fun inputModeList() = linkedMapOf(
            "None" to "",
            "Card Number" to "NUMBER",
            "Customer Mobile" to "CUST_MOBILE",
            "Order ID" to "ORDERID",
    )

    private fun transactionStatusList() = linkedMapOf(
            "All" to "",
            "Success" to "1",
            "Failure" to "2",
            "Pending" to "3",
    )
}


