package com.a2zsuvidhaa.`in`.activity.aeps2

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.AadharTransactionResponseActivity
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.activity.aeps.PidParser
import com.a2zsuvidhaa.`in`.activity.aeps2.Aeps2ViewModel.TransactionType
import com.a2zsuvidhaa.`in`.adapter.AepsChargeAdapter
import com.a2zsuvidhaa.`in`.databinding.ActivityAeps2Binding
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.KeyValue
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class Aeps2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAeps2Binding
    private lateinit var toolbar: Toolbar
    private val viewModel: Aeps2ViewModel by viewModels()

    private var progressDialog: Dialog? = null
    private var spinnerDialog: SpinnerDialog? = null

    @Inject
    lateinit var appPreference: AppPreference

    @Inject
    lateinit var volleyClient: VolleyClient

    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAeps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.aepsType = intent.getStringExtra(AppConstants.SERVICE_TYPE)!!

        toolbar = findViewById(R.id.toolbar)
        setupToolbar(toolbar, viewModel.aepsType)

        setupTransactionTypeRadioGroup()

        binding.btnProceed.setOnClickListener {

            if (!viewModel.validateInput(binding)) {
                hideKeyboard()
                return@setOnClickListener
            }

            fetchLocation(onStart = {
                progressDialog = StatusDialog.progress(this)
            }, onFetch = {
                progressDialog?.dismiss()
                showRDDeviceDialog()

            })

        }

        aepsBankList()

        binding.edBankName.setOnClickListener { spinnerDialog?.showSpinerDialog() }

        fetchLocation()
        var str = ""
        var strOldlen = 0
        binding.edAadharNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (binding.edAadharNumber.text!!.length == 12) {
                    if (!binding.edAadharNumber.text!!.contains("-")) {
                        binding.edAadharNumber.apply {
                            filters = arrayOf(InputFilter.LengthFilter(12))
                        }
                        return
                    }
                }

                str = binding.edAadharNumber.text.toString()
                val strLen = str.length
                if (strOldlen < strLen) {
                    if (strLen > 0) {
                        if (strLen == 4 || strLen == 9) {
                            str = "$str-"
                            binding.edAadharNumber.setText(str)
                            binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                        } else {
                            if (strLen == 5) {
                                if (!str.contains("-")) {
                                    var tempStr = str.substring(0, strLen - 1)
                                    tempStr += "-" + str.substring(strLen - 1, strLen)
                                    binding.edAadharNumber.setText(tempStr)
                                    binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                                }
                            }
                            if (strLen == 10) {
                                if (str.lastIndexOf("-") != 9) {
                                    var tempStr = str.substring(0, strLen - 1)
                                    tempStr += "-" + str.substring(strLen - 1, strLen)
                                    binding.edAadharNumber.setText(tempStr)
                                    binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                                }
                            }
                            strOldlen = strLen
                        }
                    } else {
                        return
                    }
                } else {
                    strOldlen = strLen
                    Log.i(
                        "MainActivity ",
                        "keyDel is Pressed ::: strLen : $strLen\n old Str Len : $strOldlen"
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.edAadharNumber.preventCopyPaste()
        binding.edAadharNumber.isLongClickable = false
        binding.edAadharNumber.setTextIsSelectable(false)

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilAadharNumber,
            binding.tilAmount,
            binding.tilBankName,
            binding.tilCustomerMobile
        )
    }


    private fun fetchChargeAndShowConfirmDialog() {

        val progressDialog = StatusDialog.progress(this)

        WebApiCall.postRequest(
            this, APIs.AEPS_COMMISSION_CHARGE, hashMapOf(
                "transaction_type" to viewModel.getTransactionTypeData(),
                "amount" to viewModel.amount
            )
        )

        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog.dismiss()

                val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")

                if (status == 1) {
                    val keyValues = arrayListOf<KeyValue>()
                    val billInfo = jsonObject.getJSONObject("data")
                    billInfo.keys().forEach { key ->
                        keyValues.add(KeyValue(key = key, value = billInfo.getString(key)))
                    }

                    showConfirmAepsTransactionDialog(keyValues)

                } else {
                    StatusDialog.failure(this@Aeps2Activity, message)
                }

            }

            override fun onFailure(message: String) {
                progressDialog.dismiss()
                StatusDialog.failure(this@Aeps2Activity, message)
            }

        })

    }


    private fun setupTransactionTypeRadioGroup() {
        binding.radioGroup.check(binding.rbCashWithdrawal.id)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbCashWithdrawal.id -> {
                    viewModel.transactionType = TransactionType.CASH_WITHDRAWAL
                    binding.tilAmount.show()
                    binding.edAmount.setText("")
                    binding.edAmount.makeFocus()
                }
                binding.rbMiniStatement.id -> {
                    viewModel.transactionType = TransactionType.MINI_STATEMENT
                    binding.tilAmount.hide()
                }
                binding.rbBalanceEnquiry.id -> {
                    viewModel.transactionType = TransactionType.BALANCE_ENQUIRY
                    binding.tilAmount.hide()
                }
                binding.rbAadhaarPay.id -> {
                    viewModel.transactionType = TransactionType.AADHAAR_PAY
                    binding.tilAmount.show()
                    binding.edAmount.setText("")
                    binding.edAmount.makeFocus()
                }
                else -> {
                }

            }
        }
    }


    private fun showRDDeviceDialog() {


        AepsDialogs.selectRDDevice2(
            context = this,
            type = "Transaction",
            onApprove = { driver ->
                viewModel.deviceName = driver.driverName
                captureData(driver.packageName)
            })
    }

    private fun captureData(packageUrl: String) {
        try {
            val pidOption = """<PidOptions ver="1.0">
       <Opts env="P" fCount="1" fType="2" format="0" iCount="0" iType="0" pCount="0" pType="0" pidVer="2.0" posh="UNKNOWN" timeout="10000"/>
    </PidOptions>"""
            val intent = Intent()
            intent.setPackage(packageUrl)
            intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
            intent.putExtra("PID_OPTIONS", pidOption)
            startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE)

        } catch (e: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val result = data.getStringExtra("PID_DATA")
                if (result != null) {
                    try {
                        val respString = PidParser.parse(result)
                        if (respString[0].equals("0", ignoreCase = true)) {
                            viewModel.pidData = result
                            if (viewModel.transactionType == TransactionType.BALANCE_ENQUIRY) {
                                showConfirmAepsTransactionDialog()
                            } else {
                                fetchChargeAndShowConfirmDialog()
                            }

                        } else MakeToast.show(this, respString[1])
                    } catch (e: java.lang.Exception) {
                        StatusDialog.failure(
                            this,
                            "Captured failed, please check biometric device is connected!"
                        )
                    }
                } else StatusDialog.failure(
                    this,
                    "Captured failed, please check biometric device is connected!"
                )

            } else StatusDialog.failure(
                this,
                "Captured failed, please check biometric device is connected!"
            )
        } else StatusDialog.failure(
            this,
            "Captured failed, please check biometric device is connected!"
        )


    }

    private fun showConfirmAepsTransactionDialog(charges: ArrayList<KeyValue> = arrayListOf()) {

        AepsDialogs.aepsTransactionConfirmation(
            context = this,
            aadhaarNumber = viewModel.aadhaarNumber,
            transactionType = viewModel.getTransactionTypeDataFullForm(),
            amount = viewModel.amount,
            bankName = viewModel.bankName,
        ).apply {

            if (charges.isNotEmpty()) {
                val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                recyclerView.show()
                recyclerView.setup(fixSize = true).adapter = AepsChargeAdapter().apply {
                    addItems(charges)
                }
            }

            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dismiss()
                aepsTransaction()
            }
        }
    }


    /*private fun aepsTransaction() {

        progressDialog = StatusDialog.fullScreenProgress(this)

        val param = HashMap<String, String>()

        param["customerNumber"] = viewModel.customerMobileNumber
        param["bankName"] = viewModel.bankCode
        param["selectedBankName"] = viewModel.bankName
        param["transactionType"] = viewModel.getTransactionTypeData()
        param["txtPidData"] = viewModel.pidData
        param["amount"] = viewModel.amount
        param["deviceName"] = viewModel.deviceName
        param["aadhaarNumber"] = viewModel.aadhaarNumber
        param["latitude"] = viewModel.latitude
        param["longitude"] = viewModel.longitude


        val url = if (viewModel.aepsType == AppConstants.AEPS_ONE)
            APIs.AEPS_TRANSACTION_ONE
        else APIs.AEPS_TRANSACTION_THREE
        WebApiCall.postRequest(this, url, param)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String?) {
                progressDialog?.dismiss()
                AppDialogs.volleyErrorDialog(this@Aeps2Activity, 0)
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                try {

                    val status = jsonObject.getString("status")
                    val message = jsonObject.optString("message")
                    when {
                        status.equals("200", ignoreCase = true) -> {
                            progressDialog?.dismiss()
                            val intent = Intent(this@Aeps2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        }
                        status.equals("300", ignoreCase = true) -> {
                            progressDialog?.dismiss()
                            val intent = Intent(this@Aeps2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        }
                        else -> {
                            parseAepsThreeData(jsonObject)
                        }
                    }

                } catch (e: JSONException) {
                    progressDialog?.dismiss()
                    val dialog: Dialog =
                            AppDialogs.transactionStatus(this@Aeps2Activity, e.message, 2)
                    val btnOK = dialog.findViewById<Button>(R.id.btn_ok)
                    btnOK.setOnClickListener { view: View? -> dialog.dismiss() }
                    dialog.show()
                }
            }

        })
    }*/

    private fun aepsTransaction() {

        progressDialog = StatusDialog.fullScreenProgress(this)

        val param = HashMap<String, String>()

        param["customerNumber"] = viewModel.customerMobileNumber
        param["bankName"] = viewModel.bankCode
        param["selectedBankName"] = viewModel.bankName
        param["transactionType"] = viewModel.getTransactionTypeData()
        param["txtPidData"] = viewModel.pidData
        param["amount"] = viewModel.amount
        param["deviceName"] = viewModel.deviceName
        param["aadhaarNumber"] = viewModel.aadhaarNumber
        param["latitude"] = viewModel.location?.latitude.toString()
        param["longitude"] = viewModel.location?.longitude.toString()


        val url = if (viewModel.aepsType == AppConstants.AEPS_ONE)
            APIs.AEPS_TRANSACTION_ONE
        else APIs.AEPS_TRANSACTION_THREE

        volleyClient.postRequest(
            url = url,
            params = param,
            onFailure = {
                progressDialog?.dismiss()
                AppDialogs.volleyErrorDialog(this@Aeps2Activity, 0)
            },
            onSuccess = {
                try {

                    val status = it.getString("status")
                    val message = it.optString("message")
                    when {
                        status.equals("200", ignoreCase = true) -> {
                            progressDialog?.dismiss()
                            val intent =
                                Intent(this@Aeps2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        }
                        status.equals("300", ignoreCase = true) -> {
                            progressDialog?.dismiss()
                            val intent =
                                Intent(this@Aeps2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        }
                        else -> {
                            parseAepsThreeData(it)
                        }
                    }

                } catch (e: JSONException) {
                    progressDialog?.dismiss()
                    val dialog: Dialog =
                        AppDialogs.transactionStatus(this@Aeps2Activity, e.message, 2)
                    val btnOK = dialog.findViewById<Button>(R.id.btn_ok)
                    btnOK.setOnClickListener { view: View? -> dialog.dismiss() }
                    dialog.show()
                }
            },
            timeOutInSecond = 240
        )

    }

    private fun parseAepsThreeData(jsonObject: JSONObject) {
        val status = jsonObject.getInt("status")
        val message = jsonObject.optString("message")
        val payId = jsonObject.optString("pay_type")

        viewModel.checkStatusInitialResponse = jsonObject
        if (status == 3 && payId == "58") checkStatusForTransaction()
        else if (status == 3 || status == 2 || status == 1) {
            if (status == 1 && viewModel.transactionType == TransactionType.CASH_WITHDRAWAL) makeFieldEmptyValue()
            progressDialog?.dismiss()

            Intent(this, AadharTransactionResponseActivity::class.java).apply {
                putExtra(AppConstants.DATA, jsonObject.toString())
                putExtra(AppConstants.ORIGIN, AppConstants.TRANSACTION_ORIGIN)
                putExtra(AppConstants.TRANSACTION_TYPE, viewModel.transactionType)
                startActivity(this)
            }

        } else {
            progressDialog?.dismiss()
            StatusDialog.failure(this, message)
        }


    }

    private fun makeFieldEmptyValue() {
        binding.edCustomerMobile.setText("")
        binding.edAmount.setText("")
        binding.edAmount.setText("")
        binding.edBankName.setText("")
        binding.edAadharNumber.setText("")

        viewModel.customerMobileNumber = AppConstants.EMPTY
        viewModel.amount = AppConstants.EMPTY
        viewModel.aadhaarNumber = AppConstants.EMPTY
        viewModel.bankName = AppConstants.EMPTY
        viewModel.bankCode = AppConstants.EMPTY
    }


    private var checkStatusCountTotal = 0
    private var checkStatusCount = 0

    private fun checkStatusForTransaction() {

        val txnId = viewModel.checkStatusInitialResponse.optString("record_id")

        lifecycleScope.launch(
            Dispatchers.Main
        ) {
            delay(4000)

            checkStatusCount += 1
            checkStatusCountTotal += 1

            volleyClient.postRequest(
                APIs.AEPS_THREE_CHECK_STATUS, hashMapOf("recordId" to txnId),
                onSuccess = { jsonObject ->

                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")



                    when (status) {
                        1, 2 -> {

                            checkStatusCount = 0
                            parseAepsThreeData(jsonObject)
                            progressDialog?.dismiss()
                        }

                        11, 33 -> {

                            if (checkStatusCount < 6) {

                                if (checkStatusCountTotal < 22) {
                                    checkStatusForTransaction()
                                } else {
                                    progressDialog?.dismiss()
                                    Intent(
                                        this@Aeps2Activity,
                                        AadharTransactionResponseActivity::class.java
                                    ).apply {
                                        putExtra(
                                            AppConstants.DATA,
                                            viewModel.checkStatusInitialResponse.toString()
                                        )
                                        putExtra(
                                            AppConstants.ORIGIN,
                                            AppConstants.TRANSACTION_ORIGIN
                                        )
                                        putExtra(
                                            AppConstants.TRANSACTION_TYPE,
                                            viewModel.transactionType
                                        )
                                        startActivity(this)
                                    }
                                }
                            } else {
                                checkStatusForTransactionFromBank()
                            }

                        }
                        else -> {

                            progressDialog?.dismiss()
                            checkStatusCount = 0

                            Intent(
                                this@Aeps2Activity,
                                AadharTransactionResponseActivity::class.java
                            ).apply {
                                putExtra(
                                    AppConstants.DATA,
                                    viewModel.checkStatusInitialResponse.toString()
                                )
                                putExtra(AppConstants.ORIGIN, AppConstants.TRANSACTION_ORIGIN)
                                putExtra(AppConstants.TRANSACTION_TYPE, viewModel.transactionType)
                                startActivity(this)
                            }

                        }
                    }
                },
                onFailure = {
                    progressDialog?.dismiss()
                    AppDialogs.volleyErrorDialog(this@Aeps2Activity, 0)
                },
                timeOutInSecond = 240
            )
        }


    }

    private fun checkStatusForTransactionFromBank() {

        val txnId = viewModel.checkStatusInitialResponse.optString("record_id")

        lifecycleScope.launch(
            Dispatchers.Main
        ) {

            checkStatusCount = 0
            checkStatusCountTotal += 1


            volleyClient.postRequest(
                APIs.AEPS_THREE_CHECK_STATUS_FROM_BANK,
                hashMapOf("record_id" to txnId),
                onSuccess = { jsonObject ->
                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")

                    if (status == 503) {
                        Intent(
                            this@Aeps2Activity,
                            AadharTransactionResponseActivity::class.java
                        ).apply {
                            putExtra(
                                AppConstants.DATA,
                                viewModel.checkStatusInitialResponse.toString()
                            )
                            putExtra(AppConstants.ORIGIN, AppConstants.TRANSACTION_ORIGIN)
                            putExtra(AppConstants.TRANSACTION_TYPE, viewModel.transactionType)
                            startActivity(this)
                        }
                    } else checkStatusForTransaction()
                },
                onFailure = {
                    progressDialog?.dismiss()
                    AppDialogs.volleyErrorDialog(this@Aeps2Activity, 0)
                },
                timeOutInSecond = 240
            )
        }


    }

    private fun aepsBankList() {
        progressDialog = StatusDialog.progress(this)
        WebApiCall.getRequest(this, APIs.GET_AEPS_BANK_LIST + "?")
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")

                if (status == 1) {
                    val aepsBankList = jsonObject.getJSONArray("data")
                    setBankList(aepsBankList)
                } else StatusDialog.failure(this@Aeps2Activity, message) { onBackPressed() }

            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(this@Aeps2Activity, message) { onBackPressed() }
            }
        })
    }

    private fun setBankList(bankJsonArray: JSONArray) {
        try {
            val bankListHashMap = HashMap<String, String>()
            bankListHashMap.clear()
            /*    val iterator: Iterator<*> = aepsObject.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next() as String
                    bankListHashMap[aepsObject.getString(key)] = key
                }*/

            for (i in 0 until bankJsonArray.length()) {

                val bankJsonObject: JSONObject = bankJsonArray.getJSONObject(i)

                bankListHashMap[bankJsonObject.getString("bankName")] =
                    bankJsonObject.getString("iinno")
            }

            val bankLists = bankListHashMap.keys.toTypedArray()

            val sortArray = bankLists.sortedArray()

            spinnerDialog =
                SpinnerDialog(this, sortArray.toList() as ArrayList<String>, "Search Bank")
            spinnerDialog?.let { spinner ->
                spinner.setCancellable(true)
                spinner.setShowKeyboard(false)
                spinner.bindOnSpinerListener { item, position ->
                    viewModel.bankName = item
                    viewModel.bankCode = bankListHashMap[item!!]!!
                    binding.edBankName.setText(item)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        if (viewModel.location != null) {
            onFetch(viewModel.location)
        } else {
            requestForLocationPhoneReadState {
                val isLocationServiceEnable = LocationService.isLocationEnabled(this)
                if (isLocationServiceEnable) {
                    onStart()
                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            if (viewModel.location == null) {
                                viewModel.location = location
                                onFetch(location)
                            }

                        }
                    })
                }
            }
        }
    }

    companion object {
        private const val RD_SERVICE_RESPONSE_DODE = 10
    }
}