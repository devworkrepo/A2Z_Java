package com.a2zsuvidhaa.`in`.util.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.*
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.model.AepsDriver
import com.a2zsuvidhaa.`in`.util.dialogConfiguration
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupAdapter

object AepsDialogs {

    fun aepsTransactionConfirmation(
        context: Context,
        aadhaarNumber: String,
        transactionType: String,
        amount: String,
        bankName: String,
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_aeps_transaction_confirmation).apply {


            findViewById<TextView>(R.id.tv_aadharNumber).text = aadhaarNumber
            findViewById<TextView>(R.id.tv_transaction_type).text = transactionType
            findViewById<TextView>(R.id.tv_amount).text = amount
            findViewById<TextView>(R.id.tv_bank_name).text = bankName
            findViewById<LinearLayout>(R.id.ll_amount).apply {
                if (transactionType == "Mini Statement" || transactionType == "Balance Enquiry") hide()
            }

            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun kycRequired(
        context: Context,
        title: String,
        description: String,
        onComplete: () -> Unit = {},
    ) = dialogConfiguration(context, R.layout.dialog_kyc_alert).apply {
        findViewById<Button>(R.id.btn_proceed).setOnClickListener {
            dismiss()
            onComplete()
        }
        findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.tv_title).text = title
        findViewById<TextView>(R.id.tv_description).text = description

        show()
    }

    fun aadhaarKycConfirm(
        context: Context,
        aadhaarNumber: String,
        mobileNumber: String
    ) = dialogConfiguration(context, R.layout.dialog_aadhaar_kyc_confirm).apply {

        findViewById<TextView>(R.id.tv_aadhaar_number).text = aadhaarNumber
        findViewById<TextView>(R.id.tv_mobile_number).text = mobileNumber

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        show()
    }

    inline fun selectRDDevice(
        context: Context,
        type: String,
        selectedDevice: String,
        crossinline onApprove: (device: String, packageUrl: String) -> Unit
    ) = dialogConfiguration(context, R.layout.dialog_rd_device).apply {

        var mSelectedDevice = selectedDevice

        val mantraPackage = "com.mantra.rdservice"
        val morphoPackage = "com.scl.rdservice"
        val startekPackage = "com.acpl.registersdk"

        var selectRDPackage = mantraPackage
        val deviceList = arrayOf("MANTRA", "MORPHO", "STARTEK")


        val dataAdapter = ArrayAdapter(
            context,
            R.layout.spinner_layout, deviceList
        )
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val spinner = findViewById<Spinner>(R.id.spn_selectDevice)
        spinner.adapter = dataAdapter
        spinner.setupAdapter(deviceList, onItemSelected = {
            AppPreference.getInstance(context).setSelectRdServiceDevice(it)
            mSelectedDevice = it
            selectRDPackage = when (it) {
                "MANTRA" -> mantraPackage
                "MORPHO" -> morphoPackage
                "STARTEK" -> startekPackage
                else -> mantraPackage
            }
        })

        spinner.setSelection(deviceList.indexOf(selectedDevice))


        findViewById<TextView>(R.id.tv_message).text =
            "Please connect biometric device and complete $type"
        findViewById<Button>(R.id.btn_proceed).setOnClickListener {
            onApprove(mSelectedDevice, selectRDPackage)
            dismiss()
        }
        show()
    }


    inline fun selectRDDevice2(
        context: Context, type: String, crossinline onApprove: (selectedDrivier : AepsDriver) -> Unit
    ) = dialogConfiguration(context, R.layout.dialog_rd_device).apply {

        val appPreference = AppPreference.getInstance(context)
        val aepsDrivers = appPreference.aepsDriviers
        val deviceNameList = aepsDrivers.map { it.driverName }.toTypedArray()

        var selectAepsDriver =
            aepsDrivers.find { it.driverName == appPreference.selectedRdServiceDevice }

        if(selectAepsDriver == null){
            selectAepsDriver = aepsDrivers.find { it.driverName == "mantra" }
        }

        val spinner = findViewById<Spinner>(R.id.spn_selectDevice)
        spinner.setupAdapter(deviceNameList, onItemSelected = { deviceName ->
            selectAepsDriver = aepsDrivers.find { it.driverName == deviceName }

        })

        spinner.setSelection(deviceNameList.indexOf(selectAepsDriver!!.driverName))
        findViewById<TextView>(R.id.tv_message).text =
            "Please connect biometric device and complete $type"

        findViewById<Button>(R.id.btn_proceed).setOnClickListener {
            appPreference.setSelectRdServiceDevice(selectAepsDriver!!.driverName)
            onApprove(selectAepsDriver!!)
            dismiss()
        }
        show()
    }
}