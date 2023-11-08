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

class UserFilterDialog(
    private var inputMode: String,
    private var inputText: String,
) {


    private var atInputMode: AutoCompleteTextView? = null
    private var tilInput: TextInputLayout? = null
    private var edInput: EditText? = null


    fun showDialog(
        activity: Activity,
        onSearchClick: (
            inputMode : String,
            inputText : String
        ) -> Unit
    ) = setupDialog(activity).apply {


        atInputMode = findViewById(R.id.actv_input_mode)
        tilInput = findViewById(R.id.til_input)
        edInput = findViewById(R.id.ed_input)

        setupInputMode()

        findViewById<LinearLayout>(R.id.ll_search)?.setOnClickListener{

            inputText = edInput?.text.toString()
            dismiss()
            onSearchClick(inputMode,inputText)
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

    private fun onInputModeChange(){
        edInput?.text = null
        when(inputMode){
            "MOB"->{
                tilInput?.hint = "Enter Mobile Number"
                edInput?.inputType = InputType.TYPE_CLASS_NUMBER
                edInput?.filters = arrayOf(InputFilter.LengthFilter(10))
            }
            "ID"->{
                tilInput?.hint = "Enter User ID"
                edInput?.inputType = InputType.TYPE_CLASS_NUMBER
                edInput?.filters = arrayOf(InputFilter.LengthFilter(20))
            }
            "NAME"->{
                tilInput?.hint = "Enter User Name"
                edInput?.inputType = InputType.TYPE_CLASS_TEXT
                edInput?.filters = arrayOf(InputFilter.LengthFilter(20))
            }
        }
    }

    private fun setupDialog(
        activity: Activity,
    ): BottomSheetDialog {


        //init dialog
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_user_filter)

        bottomSheetDialog.show()
        return bottomSheetDialog
    }

    private fun inputModeList() = linkedMapOf(
            "None" to "",
            "User ID" to "ID",
            "User Name" to "NAME",
            "Mobile Number" to "MOB",
    )

}


