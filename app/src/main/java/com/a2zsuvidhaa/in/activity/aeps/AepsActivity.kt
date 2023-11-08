package com.a2zsuvidhaa.`in`.activity.aeps


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.RequestHandler
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.activity.AadharTransactionResponseActivity
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.goToMainActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class AepsActivity : AppCompatActivity() {


    private var ed_aadharNumber: EditText? = null
    private var btnCapture: Button? = null
    private var btnReset: ImageButton? = null
    private var spn_serviceType: Spinner? = null
    private var ll_amount: LinearLayout? = null
    private var ed_customerNumber: EditText? = null
    private var ed_Amount: EditText? = null
    private var atv_bankName: AutoCompleteTextView? = null
    private var progress_bank: ProgressBar? = null
    private var progressProceed: ProgressBar? = null
    private var sv_layout: ScrollView? = null
    private var spn_selectDevice: Spinner? = null


    private var deviceName = "MANTRA"
    private var customerMobileNumber = "455"
    private var amount = ""
    private var aadharNumber = ""
    private var bankCode: String? = ""
    private var bankName = ""
    private var pidData = ""
    private var transactionType = "BE"
    private var aepsType: String? = AppConstants.EMPTY

    @Inject
    lateinit var appPreference: AppPreference

    val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var latitude: String = AppConstants.EMPTY
    private var longitude: String = AppConstants.EMPTY

    private var progressDialog: Dialog? = null
    private var aepsTransactionDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aeps)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "AEPS"
        // getSupportActionBar().setSubtitle("Balance - Rs. "+SharedPref.getInstance(this).getUserBalance());
        initView()
        setReqtuestTo()
        setDeviceType()
        aepsBankList()
        ed_aadharNumber!!.setText("")
        ed_aadharNumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                str = ed_aadharNumber!!.text.toString()
                val strLen = str.length
                if (strOldlen < strLen) {
                    if (strLen > 0) {
                        if (strLen == 4 || strLen == 9) {
                            str = "$str-"
                            ed_aadharNumber!!.setText(str)
                            ed_aadharNumber!!.setSelection(ed_aadharNumber!!.text.length)
                        } else {
                            if (strLen == 5) {
                                if (!str.contains("-")) {
                                    var tempStr = str.substring(0, strLen - 1)
                                    tempStr += "-" + str.substring(strLen - 1, strLen)
                                    ed_aadharNumber!!.setText(tempStr)
                                    ed_aadharNumber!!.setSelection(ed_aadharNumber!!.text.length)
                                }
                            }
                            if (strLen == 10) {
                                if (str.lastIndexOf("-") != 9) {
                                    var tempStr = str.substring(0, strLen - 1)
                                    tempStr += "-" + str.substring(strLen - 1, strLen)
                                    ed_aadharNumber!!.setText(tempStr)
                                    ed_aadharNumber!!.setSelection(ed_aadharNumber!!.text.length)
                                }
                            }
                            strOldlen = strLen
                        }
                    } else {
                        return
                    }
                } else {
                    strOldlen = strLen
                    Log.i("MainActivity ", "keyDel is Pressed ::: strLen : $strLen\n old Str Len : $strOldlen")
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        btnCapture!!.setOnClickListener { view: View? ->

            if (latitude.isEmpty() && longitude.isEmpty()) {
                progressDialog = StatusDialog.progress(this@AepsActivity)
                fetchLocation {
                    progressDialog?.dismiss()
                    captureData()
                }
            } else captureData()


        }
        btnReset!!.setOnClickListener { view: View? -> resetData() }

        fetchLocation {}
    }

    var str = ""
    var strOldlen = 0
    private fun initView() {
        ed_aadharNumber = findViewById(R.id.ed_aadharNumber)
        btnCapture = findViewById(R.id.btnCapture)
        btnReset = findViewById(R.id.btnReset)
        spn_serviceType = findViewById(R.id.spn_serviceType)
        ll_amount = findViewById(R.id.ll_amount)
        ed_customerNumber = findViewById(R.id.ed_customerNumber)
        ed_Amount = findViewById(R.id.ed_Amount)
        /*        spn_bank = findViewById(R.id.spn_bank);*/atv_bankName = findViewById(R.id.atv_bank_name)
        spn_selectDevice = findViewById(R.id.spn_selectDevice)
        progress_bank = findViewById(R.id.progress_bank)
        progressProceed = findViewById(R.id.progressProceed)
        sv_layout = findViewById(R.id.sv_layout)
        aepsType = intent.getStringExtra(AppConstants.SERVICE_TYPE)
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        tvTitle.text = aepsType
    }

    private fun setReqtuestTo() {
        val requestToList = arrayOf("Enquiry", "Withdrawal", "Mini Statement")
        val dataAdapter = ArrayAdapter(this,
                R.layout.spinner_layout, requestToList)
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        spn_serviceType!!.adapter = dataAdapter
        spn_serviceType!!.setSelection(1)
        spn_serviceType!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (spn_serviceType!!.selectedItem.toString().equals(requestToList[1], ignoreCase = true)) {
                    ll_amount!!.visibility = View.VISIBLE
                    transactionType = "CW"
                } else {
                    ll_amount!!.visibility = View.GONE
                    transactionType = if (spn_serviceType!!.selectedItem.toString().equals(requestToList[0], ignoreCase = true)) "BE" else "MS"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setDeviceType() {
        val deviceList = arrayOf("MANTRA", "MORPHO")
        val dataAdapter = ArrayAdapter(this,
                R.layout.spinner_layout, deviceList)
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        spn_selectDevice!!.adapter = dataAdapter
        spn_selectDevice!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                deviceName = if (spn_selectDevice!!.selectedItem.toString().equals(deviceList[0], ignoreCase = true)) {
                    appPreference.setSelectRdServiceDevice("MANTRA")
                    "MANTRA"
                } else {
                    appPreference.setSelectRdServiceDevice("MORPHO")
                    "MORPHO"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val selectedDevice = appPreference.selectedRdServiceDevice
        if (selectedDevice.isNotEmpty()) {

            spn_selectDevice?.setSelection(dataAdapter.getPosition(selectedDevice))
        }
    }

    private fun  aepsBankList() {
            val url: String = (APIs.GET_AEPS_BANK_LIST + "?" + APIs.USER_TAG + "=" + AppPreference.getInstance(this).getId()
                    + "&" + APIs.TOKEN_TAG + "=" + AppPreference.getInstance(this).getToken())
            val request: StringRequest = object : StringRequest(Method.GET, url,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response)
                            val status = jsonObject.getString("status")
                            val message = jsonObject.getString("message")
                            if (status.equals("1", ignoreCase = true)) {
                                sv_layout!!.visibility = View.VISIBLE
                                progress_bank!!.visibility = View.GONE
                                val aepsBankList = jsonObject.getJSONObject("data")
                                setBankList(aepsBankList)
                            } else if (status.equals("200", ignoreCase = true)) {
                                val intent = Intent(this, AppInProgressActivity::class.java)
                                intent.putExtra("message", message)
                                intent.putExtra("type", 0)
                                startActivity(intent)
                            } else if (status.equals("300", ignoreCase = true)) {
                                val intent = Intent(this, AppInProgressActivity::class.java)
                                intent.putExtra("message", message)
                                intent.putExtra("type", 1)
                                startActivity(intent)
                            } else MakeToast.show(this, message)
                        } catch (e: JSONException) {
                            MakeToast.show(this, "Something went wrong! please contanct to admin")
                            finish()
                        }
                    },
                    Response.ErrorListener { error: VolleyError? ->
                        MakeToast.show(this, "Something went wrong! please contanct to admin")
                        finish()
                    }) {}
            RequestHandler.getInstance(this).addToRequestQueue(request)
            request.retryPolicy = DefaultRetryPolicy(
                    TimeUnit.SECONDS.toMillis(20).toInt(),
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }

    private fun setBankList(aepsObject: JSONObject) {
        try {
            val bankListHashMap = HashMap<String, String>()
            bankListHashMap.clear()
            val iterator: Iterator<*> = aepsObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                bankListHashMap[aepsObject.getString(key)] = key
            }
            val bankLists = bankListHashMap.keys.toTypedArray()
            val arrayAdapter = ArrayAdapter(this,
                    android.R.layout.select_dialog_item, bankLists)
            atv_bankName!!.threshold = 1
            atv_bankName!!.setAdapter(arrayAdapter)
            atv_bankName!!.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                bankCode = bankListHashMap[atv_bankName!!.text.toString()]
                bankName = atv_bankName!!.text.toString()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val isValidInput: Boolean
        private get() {

            var isValid = false
            customerMobileNumber = ed_customerNumber!!.text.toString()
            aadharNumber = ed_aadharNumber!!.text.toString().replace("-", "")
            amount = ed_Amount!!.text.toString()
            Log.e("adhar", "" + aadharNumber)
            if (!customerMobileNumber.isEmpty() && customerMobileNumber.length >= 10) {
                if (!atv_bankName!!.text.toString().isEmpty() && !bankCode!!.isEmpty()) {
                    if (aadharNumber.length == 12) {
                        if (amount.isNotEmpty() && !amount.equals("0", ignoreCase = true) && amount.toInt() >= 100 && amount.toInt() <= 10000 ||
                                !spn_serviceType!!.selectedItem.toString().equals("Withdrawal", ignoreCase = true)) {
                            isValid = true
                        } else MakeToast.show(this, "Amount should be between 100 - 10000 only.")
                    } else MakeToast.show(this, "Aadhar number can't be empty! and length should be 12 digit")
                } else MakeToast.show(this, "Select a valid bank name! bank can't be empty!")
            } else MakeToast.show(this, "Customer number can't be empty! and length should be 10 digit")
            return isValid
        }


    private fun captureData() {
        if (isValidInput) {
            val MANTRA_PACKAGE_URL = "com.mantra.rdservice"
            val MORPHO_PACKAGE_URL = "com.scl.rdservice"
            val STARTEK_PACKAGE_URL = "com.acpl.registersdk"


            try {
                val pidOption = """<PidOptions ver="1.0">
       <Opts env="P" fCount="1" fType="2" format="0" iCount="0" iType="0" pCount="0" pType="0" pidVer="2.0" posh="UNKNOWN" timeout="10000"/>
    </PidOptions>"""
                val intent = Intent()
                // intent.setPackage(connectedDevicePackage)
                intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                intent.putExtra("PID_OPTIONS", pidOption)
                startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE)

            } catch (e: Exception) {

                MakeToast.show(this, "No device is connected! please reconnect and try again")


            }
        }
    }

    fun resetData() {
        ed_aadharNumber!!.setText("")
        ed_customerNumber!!.setText("")
        atv_bankName!!.setText("")
        ed_Amount!!.setText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RD_SERVICE_RESPONSE_DODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        val result = data.getStringExtra("PID_DATA")
                        if (result != null) {
                            try {
                                val respString = PidParser.parse(result)
                                if (respString[0].equals("0", ignoreCase = true)) {
                                    pidData = result
                                    confirmDialog()
                                } else MakeToast.show(this, respString[1])
                            } catch (e: XmlPullParserException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun aepsTransaction() {

        progressDialog = StatusDialog.fullScreenProgress(this)
        setViewEnableDisable(false)

        val param = HashMap<String, String>()

        if (amount.equals("", ignoreCase = true)) amount = "0"
        if (transactionType == "BE" || transactionType == "MS") amount = "0"


        param["customerNumber"] = customerMobileNumber
        param["bankName"] = bankCode!!
        param["selectedBankName"] = bankName
        param["transactionType"] = transactionType
        param["txtPidData"] = pidData
        param["amount"] = amount
        param["deviceName"] = deviceName
        param["aadhaarNumber"] = aadharNumber
        param["latitude"] = latitude
        param["longitude"] = longitude


        val url = if (aepsType == AppConstants.AEPS_ONE)
            APIs.AEPS_TRANSACTION_ONE
        else APIs.AEPS_TRANSACTION_THREE
        WebApiCall.postRequest(this, url, param)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String?) {
                progressDialog?.dismiss()
                AppDialogs.volleyErrorDialog(this@AepsActivity, 0)
                setViewEnableDisable(true)
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                setViewEnableDisable(true)


                try {
                    val status = jsonObject.getString("status")
                    val message = jsonObject.optString("message")
                    if (status.equals("200", ignoreCase = true)) {
                        progressDialog?.dismiss()
                        val intent = Intent(this@AepsActivity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else if (status.equals("300", ignoreCase = true)) {
                        progressDialog?.dismiss()
                        val intent = Intent(this@AepsActivity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else {
                        /*if (aepsType == AppConstants.AEPS_ONE)
                            parseAepsOneData(jsonObject)
                        else if (aepsType == AppConstants.AEPS_THREE)*/
                        parseAepsThreeData(jsonObject)

                    }

                } catch (e: JSONException) {
                    progressDialog?.dismiss()
                    val dialog: Dialog = AppDialogs.transactionStatus(this@AepsActivity, e.message, 2)
                    val btnOK = dialog.findViewById<Button>(R.id.btn_ok)
                    btnOK.setOnClickListener { view: View? -> dialog.dismiss() }
                    dialog.show()
                }
            }

        })
    }

    /*private fun parseAepsOneData(jsonObject: JSONObject) {

        val status = jsonObject.optString("status")
        val message = jsonObject.optString("message")

        if (status.equals("Success", ignoreCase = true)
                || (aepsType == AppConstants.AEPS_THREE && (status == "1" || status == "3"))) {

            if (transactionType.equals("MS", ignoreCase = true)) {
                val intent = Intent(this@AepsActivity, MiniStatementActivity::class.java)
                intent.putExtra("balance", "" + jsonObject.optString("availableBalance"))
                intent.putExtra("data", "" + jsonObject.optString("statments"))
                intent.putExtra("id", "" + jsonObject.optString("txnId"))
                startActivity(intent)
            } else {
                aepsTransactionDialog = AppDialogs.aepsResponseDialog(this@AepsActivity, 1)

                aepsTransactionDialog?.apply {

                    val tvMobile = findViewById<TextView>(R.id.tv_mobile)
                    val tvAadharnumber = findViewById<TextView>(R.id.tv_aadharNumber)
                    val tvBankrrnumber = findViewById<TextView>(R.id.tv_bankRRNumber)
                    val tvTransactionamount = findViewById<TextView>(R.id.tv_transactionAmount)
                    val tvTransactionid = findViewById<TextView>(R.id.tv_transactionId)
                    val tvTransactiontype = findViewById<TextView>(R.id.tv_transactionType)
                    val tvTransactiontime = findViewById<TextView>(R.id.tv_transactionTime)
                    val tvAvailbalance = findViewById<TextView>(R.id.tv_availBalance)
                    tvMobile.text = customerMobileNumber
                    tvAadharnumber.text = aadharNumber
                    tvBankrrnumber.text = jsonObject.optString("bankRRN")
                    tvTransactionamount.text = jsonObject.optString("transactionAmount")
                    tvTransactiontype.text = jsonObject.optString("transactionType")
                    tvTransactionid.text = jsonObject.optString("fpTransactionId")
                    tvTransactiontime.text = jsonObject.optString("txnTime")
                    tvAvailbalance.text = jsonObject.optString("availableBalance")


                    val btnOk = findViewById<Button>(R.id.btn_ok)
                    btnOk.setOnClickListener { view: View? -> dismiss() }
                    setOnDismissListener {
                        if (transactionType.equals("CW", ignoreCase = true)) {
                            val intent = Intent(this@AepsActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                    show()
                }
            }
        } else {
            progressDialog?.dismiss()
            val dialog: Dialog = AppDialogs.transactionStatus(this@AepsActivity, message, 2)
            val btnOK = dialog.findViewById<Button>(R.id.btn_ok)
            btnOK.setOnClickListener { view: View? -> dialog.dismiss() }
            dialog.show()
        }
    }*/

    private fun parseAepsThreeData(jsonObject: JSONObject) {
        val status = jsonObject.getInt("status")
        val message = jsonObject.optString("message")
        val payId = jsonObject.optString("pay_type")

        if (status == 3 && payId == "58") checkStatusForAeps3(jsonObject)
        else if (status == 3 || status == 2 || status == 1) {
            if (status == 1 && transactionType.equals("CW", ignoreCase = true)) makeFieldEmptyValue()
            progressDialog?.dismiss()

            Intent(this, AadharTransactionResponseActivity::class.java).apply {
                    putExtra(AppConstants.DATA, jsonObject.toString())
                    putExtra(AppConstants.SERVICE_TYPE, aepsType)
                putExtra(AppConstants.ORIGIN, AppConstants.AEPS)
                startActivity(this)
                }

        } else {
            progressDialog?.dismiss()
            StatusDialog.failure(this, message)
        }


    }

    private fun makeFieldEmptyValue() {
        ed_customerNumber?.setText("")
        ed_Amount?.setText("")
        ed_aadharNumber?.setText("")
        atv_bankName?.setText("")

        customerMobileNumber = AppConstants.EMPTY
        amount = AppConstants.EMPTY
        aadharNumber = AppConstants.EMPTY
        bankName = AppConstants.EMPTY
        bankCode = AppConstants.EMPTY
    }


    /*private fun parseAepsTransactionResponse(status: String, jsonObject: JSONObject, message: String) {

        if (status.equals("Success", ignoreCase = true)
                || (aepsType == AppConstants.AEPS_THREE && (status == "1" || status == "3"))) {
            if (transactionType.equals("MS", ignoreCase = true)) {
                val intent = Intent(this@AepsActivity, MiniStatementActivity::class.java)
                intent.putExtra("balance", "" + jsonObject.optString("availableBalance"))
                intent.putExtra("data", "" + jsonObject.optString("statments"))
                intent.putExtra("id", "" + jsonObject.optString("txnId"))
                startActivity(intent)
            } else {

                val payType = jsonObject.optString("payType")
                val txnId = jsonObject.optString("txnId")
                if (aepsType == AppConstants.AEPS_THREE)
                    if (status == "3" && payType == "58") checkStatusForAeps3(txnId)

                Intent(this, AadharTransactionResponseActivity::class.java).apply {
                    putExtra("data", jsonObject.toString())
                    if (aepsType == AppConstants.AEPS_ONE) {
                        putExtra(AppConstants.SERVICE_TYPE, AppConstants.AEPS_ONE)
                        startActivity(this)
                    } else if (aepsType == AppConstants.AEPS_THREE && (status == "1")) {
                        putExtra(AppConstants.SERVICE_TYPE, AppConstants.AEPS_THREE)
                        startActivity(this)
                    }
                }


            }
        } else {
            progressDialog?.dismiss()
            val dialog: Dialog = AppDialogs.transactionStatus(this@AepsActivity, message, 2)
            val btnOK = dialog.findViewById<Button>(R.id.btn_ok)
            btnOK.setOnClickListener { view: View? -> dialog.dismiss() }
            dialog.show()
        }
    }
*/
    private var aepsThreeCheckStatusCount = 0
    private fun checkStatusForAeps3(preJsonObject: JSONObject) {

        val txnId = preJsonObject.optString("txn_id")

        lifecycleScope.launch(Dispatchers.Main
        ) {
            delay(5000)

            aepsThreeCheckStatusCount += 1

            WebApiCall.postRequest(this@AepsActivity, APIs.AEPS_THREE_CHECK_STATUS, hashMapOf("recordId" to txnId))
            WebApiCall.webApiCallback(object : WebApiCallListener {
                override fun onFailure(message: String) {
                    progressDialog?.dismiss()
                    AppDialogs.volleyErrorDialog(this@AepsActivity, 0)

                }

                override fun onSuccessResponse(jsonObject: JSONObject) {


                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")



                    when (status) {
                        1, 2 -> {

                            aepsThreeCheckStatusCount = 0
                            parseAepsThreeData(jsonObject)
                            progressDialog?.dismiss()
                        }

                        11, 33 -> {

                            if (aepsThreeCheckStatusCount < 3) {
                                checkStatusForAeps3(preJsonObject)
                            } else {

                                aepsThreeCheckStatusCount = 0
                                StatusDialog.pending(this@AepsActivity, "Transaction in Pending, please check aeps report").apply {
                                    setOnDismissListener {
                                        if (!BuildConfig.DEBUG)
                                            goToMainActivity()
                                    }
                                }
                                progressDialog?.dismiss()
                            }

                        }
                        else -> {

                            progressDialog?.dismiss()
                            aepsThreeCheckStatusCount = 0
                            StatusDialog.failure(this@AepsActivity, message)

                        }
                    }

                }

            })
        }


    }

    private fun setViewEnableDisable(enable: Boolean) {
        if (enable) {

            btnCapture!!.visibility = View.VISIBLE
            btnReset!!.visibility = View.VISIBLE
            ed_customerNumber!!.alpha = 1f
            ed_customerNumber!!.isEnabled = true
            ed_aadharNumber!!.alpha = 1f
            ed_aadharNumber!!.isEnabled = true
            ed_Amount!!.alpha = 01f
            ed_Amount!!.isEnabled = true
        } else {

            btnCapture!!.visibility = View.GONE
            btnReset!!.visibility = View.GONE
            ed_customerNumber!!.alpha = 0.5f
            ed_customerNumber!!.isEnabled = false
            ed_aadharNumber!!.alpha = 0.5f
            ed_aadharNumber!!.isEnabled = false
            ed_Amount!!.alpha = 0.5f
            ed_Amount!!.isEnabled = false
        }
    }

    private fun confirmDialog() {

        val mTxnType: String = when (transactionType) {
            "MS" -> "Mini Statement"
            else -> if (transactionType == "BE") "Balance Enquiry"
            else "Cash Withdrawal"
        }

        AepsDialogs.aepsTransactionConfirmation(
                context = this,
                aadhaarNumber = aadharNumber,
                transactionType = mTxnType,
                amount = amount,
                bankName = bankName
        ).apply {
            findViewById<Button>(R.id.btn_confirm).setOnClickListener {

                dismiss()
                aepsTransaction()
            }
        }
    }

    private fun fetchLocation(onFetch: (Location?) -> Unit) {
        val isLocationServiceEnable = LocationService.isLocationEnabled(this)
        if (isLocationServiceEnable) {

            requestForLocationPhoneReadState {
                LocationService.getCurrentLocation(mLocationManager)
                LocationService.setupListener(object : LocationService.MLocationListener {
                    override fun onLocationChange(location: Location) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        AppLog.d("Location fetched : $location")
                        onFetch(location)
                    }
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    companion object {
        private const val TAG = "AepsActivity"
        private const val RD_SERVICE_RESPONSE_DODE = 10
    }


}
