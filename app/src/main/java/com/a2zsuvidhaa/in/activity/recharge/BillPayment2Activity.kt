package com.a2zsuvidhaa.`in`.activity.recharge

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.activity.login.globalLocation
import com.a2zsuvidhaa.`in`.adapter.BillDetailAdapter
import com.a2zsuvidhaa.`in`.databinding.ActivityBillPayment2Binding
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.KeyValue
import com.a2zsuvidhaa.`in`.model.Provider
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.enums.ProviderType
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class BillPayment2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityBillPayment2Binding
    private lateinit var provider: Provider
    private lateinit var requestType: String
    private lateinit var providerType: ProviderType
    private lateinit var appPreference: AppPreference

    private var isDobField = false
    private var isEmailField = false

    private var strAmount: String = AppConstants.EMPTY
    private var strNumber: String = AppConstants.EMPTY
    private var strMobile: String = AppConstants.EMPTY
    private var strEmail: String = AppConstants.EMPTY
    private var strDob: String = AppConstants.EMPTY
    private var billContext: String = AppConstants.EMPTY
    private var fetchAmount: String = AppConstants.EMPTY
    private var bbpsType: String = AppConstants.BBPS_ONE
    private var dueDate: String = AppConstants.EMPTY
    private var customerName: String = AppConstants.EMPTY

    private var progressDialog: Dialog? = null
    private lateinit var toolbar: Toolbar


    @Inject
    lateinit var volleyClient: VolleyClient




    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var location : Location? = globalLocation

    private val appViewModel : AppViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPayment2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        provider = intent.getParcelableExtra(ProviderActivity.PROVIDER)!!
        requestType = intent.getStringExtra("requestType")!!
        providerType = intent.getSerializableExtra(ProviderActivity.PROVIDER_TYPE) as ProviderType
        appPreference = AppPreference.getInstance(this)

        setupExtraParams()

        initViews()
        setupToolbar()
        setupMinMaxLength()
        setupAutoCompleteView()

        binding.btnProceed.setOnClickListener {
            onProceedButtonClick()
        }

        binding.edAmount1.afterTextChange {
            amountTextChangeListener(binding.edAmount1, binding.tilAmount1)
        }
        binding.edAmount2.afterTextChange {
            amountTextChangeListener(binding.edAmount2, binding.tilAmount2)
        }

        binding.tvAmountText.text = "Wallet available balance: ₹${AppPreference.getInstance(this).userBalance}"

        fetchUserBalance()

        binding.tilAmount1.hint =provider.amountMessage
        binding.tilAmount2.hint =provider.amountMessage

        fetchLocation ()

        ViewUtil.resetErrorOnTextInputLayout(binding.tilAmount1,binding.tilAmount2)

        subscribers()

        binding.llRefresh.setOnClickListener {
            appViewModel.fetchBalanceInfo()
        }
    }

    private fun subscribers(){
        appViewModel.balanceObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this)
                }
                is Resource.Success -> {

                    progressDialog?.dismiss()
                    appPreference.userBalance = it.data.balance.userBalance
                    binding.tvAmountText.text =
                        "Wallet available balance: ₹" + appPreference.userBalance

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun enableButton(inputLayout : TextInputLayout){
        inputLayout.isErrorEnabled = false
        binding.btnProceed.alpha = 1f
        binding.btnProceed.isClickable = true
        binding.btnProceed.isEnabled = true
    }

    private fun disableButtonWithError(inputLayout: TextInputLayout,error : String){
        inputLayout.isErrorEnabled = true
        inputLayout.error = error
        binding.btnProceed.alpha = 0.5f
        binding.btnProceed.isClickable = false
        binding.btnProceed.isEnabled = false
    }

    private fun amountTextChangeListener(edAmount: EditText, inputLayout: TextInputLayout) {

        if(provider.isAmountEditable != 1){ return }

        var textAmount = edAmount.text.toString()
        if (textAmount.isEmpty()) textAmount = "0"
        val doubleAmount = textAmount.toDouble()

        if(doubleAmount == 0.0){
            disableButtonWithError(inputLayout,"Enter valid amount")
        }
        else if(doubleAmount < provider.minAmount && provider.minAmount != 0.0){
            disableButtonWithError(inputLayout, "Enter min amount rs. ${provider.minAmount}")
        }
        else if(doubleAmount > provider.maxAmount && provider.maxAmount != 0.0){
            disableButtonWithError(inputLayout, "Enter max amount rs. ${provider.maxAmount}")
        }
        else enableButton(inputLayout)
    }

    private fun onProceedButtonClick() {
        if (isValid())
            if (bbpsType.isNotEmpty() || binding.btnProceed.text.toString() == AppConstants.FETCH_BILL) {
                try {
                    if (strAmount.toDouble() <= appPreference.userBalance.toDouble()) {
                        if (AppConstants.FETCH_BILL == binding.btnProceed.text.toString()) fetchBillInfo()
                        else proceedForBillPayment()
                    } else {
                        Snackbar.make(binding.coordinatorLayout, "Insufficient wallace balance", Snackbar.LENGTH_LONG).apply {
                            setBackgroundTint(ContextCompat.getColor(this@BillPayment2Activity, R.color.red))
                            show()
                        }
                    }
                } catch (e: Exception) {
                    showToast("Something went wrong, please contact with admin")
                }
            } else showToast("Select BBPS type for payment")
    }

    private fun fetchBillInfo() {

        val progressTitle = if (providerType == ProviderType.FASTTAG)
            "Fetching vehicle info..."
        else "Fetching bill info..."
        progressDialog = StatusDialog.progress(this, progressTitle)

        val url: String = ("${APIs.VIEW_ELECTRICITY_BILL}?number=$strNumber&dob=$strDob" +
                "&customerMobileNumber=${binding.edMobile.text}&provider=${provider.id}")
        WebApiCall.getRequest(this, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject)  {
                progressDialog?.dismiss()
                onFetchBillResponse(jsonObject)
            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                MakeToast.show(this@BillPayment2Activity, "Unable to fetch bill try it manually$message")
            }

        })
    }

    private fun onFetchBillResponse(jsonObject: JSONObject) {
        try {
            val status = jsonObject.getInt("status")
            when (status) {
                1 -> {
                    binding.cvOne.hide()
                    binding.cvTwo.show()
                    binding.cvBbpsType.show()
                    binding.btnProceed.text = setupPayButtonText()

                    binding.tvNumberPayWithFetch.text = binding.edNumber.text.toString()

                    val keyValues = arrayListOf<KeyValue>()
                    val billInfo = jsonObject.getJSONObject("billInfo")
                    billInfo.keys().forEach { key ->
                        val value = billInfo.optString(key)


                        if (key.equals("CustomerName", true))
                            customerName = value
                        if (key.equals("Duedate", true))
                            dueDate = value


                        if (key.equals("context", true) ||
                                key.equals("Billamount", true)) {
                            when (key) {
                                "context" -> billContext = value
                                else -> {
                                    fetchAmount = value
                                    binding.tvAmount2.text = value
                                    binding.edAmount1.setText(value)
                                    binding.edAmount2.setText(value)
                                    binding.edAmount1.requestFocus()
                                    binding.edAmount2.requestFocus()
                                }
                            }
                        } else keyValues.add(KeyValue(key, value))
                    }

                    val mAdapter = BillDetailAdapter()
                    mAdapter.context = this
                    mAdapter.addItems(keyValues)
                    binding.recyclerView.apply {
                        setHasFixedSize(false)
                        layoutManager = LinearLayoutManager(this@BillPayment2Activity)
                        adapter = mAdapter
                    }

                }
                200 -> {
                    val message = jsonObject.getString("message")
                    val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                    intent.putExtra("message", message)
                    intent.putExtra("type", 0)
                    startActivity(intent)
                }
                300 -> {
                    val message = jsonObject.getString("message")
                    val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                    intent.putExtra("message", message)
                    intent.putExtra("type", 1)
                    startActivity(intent)
                }
                0 -> {
                    val message = jsonObject.getString("message")
                    MakeToast.show(this@BillPayment2Activity, message)
                }
                else -> {
                    val message = jsonObject.getString("message")
                    MakeToast.show(this@BillPayment2Activity, message)
                }
            }
        } catch (e: JSONException) {
            MakeToast.show(this@BillPayment2Activity, "Something went wrong -${e.message}")
        }
    }

    private fun proceedForBillPayment() {

        //first fetch commission and Convenience fee
        progressDialog = StatusDialog.progress(this, "Processing...")
        var url = if (bbpsType == AppConstants.BBPS_ONE) APIs.BBPSONESLIP else APIs.BBPSTWOSLIP
        url = "$url?number=$strNumber&amount=$strAmount&provider=${provider.id}"



        WebApiCall.getRequest(this, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                try {

                    val status = jsonObject.getString("status")
                    when {
                        status.equals("1", ignoreCase = true) -> {

                            val billDetails = jsonObject.getJSONObject("billDetails")
                            var commission = billDetails.optString("Commission")
                            if (commission.isEmpty()) commission = "0"

                            var convenienceFee = billDetails.optString("Convenience Fee")
                            if (convenienceFee.isEmpty()) convenienceFee = "0"


                            fetchLocation(onStart = {
                                progressDialog = StatusDialog.progress(this@BillPayment2Activity,"Validating Location")
                            }) {
                                progressDialog?.dismiss()
                                billPayConfirmDialog(commission, convenienceFee)
                            }
                        }
                        status.equals("200", ignoreCase = true) -> {
                            val message = jsonObject.getString("message")
                            val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        }
                        status.equals("300", ignoreCase = true) -> {
                            val message = jsonObject.getString("message")
                            val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 1)
                            startActivity(intent)
                        }
                        status.equals("0", ignoreCase = true) -> {
                            val message = jsonObject.getString("message")
                            MakeToast.show(this@BillPayment2Activity, message)
                        }
                        else -> {
                            MakeToast.show(this@BillPayment2Activity, "No offer found for this operator(c)")
                        }
                    }
                } catch (e: JSONException) {
                    MakeToast.show(this@BillPayment2Activity, "No offer found for this operator")
                }
            }

            override fun onFailure(message: String) {
                MakeToast.show(this@BillPayment2Activity, "No offer found for this operator")
                progressDialog?.dismiss()

            }

        })

    }

    private fun billPayConfirmDialog(commission: String, convenienceFee: String) {


        Dialogs.confirmBBPSPayment(this,
                commission = commission,
                convenienceFee = convenienceFee,
                number = strNumber,
                numberTitle = provider.dealerName,
                amount = strAmount,
                operator = provider.providerName,
                bbpsType = bbpsType).apply {
            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dismiss()
                finalPayBill()
            }
        }
    }

    private fun finalPayBill() {

        progressDialog = StatusDialog.fullScreenProgress(this)

        val url = if (bbpsType == AppConstants.BBPS_ONE) APIs.BBPSONE else APIs.BBPSTWO
        val param =  hashMapOf(
            "context" to billContext,
            "number" to strNumber,
            "provider" to provider.id,
            "amount" to strAmount,
            "CustomerName" to customerName,
            "bill_due_date" to dueDate,
            "customerMobileNumber" to strMobile,
            "latitude" to location?.latitude.toString(),
            "longitude" to location?.longitude.toString(),

            )

        volleyClient.postRequest(
            url,param, onFailure = {
                progressDialog?.dismiss()
                AppDialogs.volleyErrorDialog(this@BillPayment2Activity, 0)
            },
            onSuccess = {jsonObject->
                progressDialog?.dismiss()
                try {
                    val status = jsonObject.getInt("statusId")
                    val message = jsonObject.getString("message")
                    if (status == 1 || status == 3 || status == 24 || status == 34) {

                        val payId = jsonObject.optString("payid")
                        val txnTime = jsonObject.optString("txnTime")
                        val operatorRef = jsonObject.optString("operator_ref")
                        val statusDescription = jsonObject.optString("status")
                        val billerName = jsonObject.optString("billerName")
                        val providerName = jsonObject.optString("providerName")
                        val amount = jsonObject.optString("amount")
                        val type = jsonObject.optString("type")
                        val recordId = jsonObject.optString("recordId")


                        Intent(this@BillPayment2Activity,
                            BillRechargeResultActivity::class.java).apply {
                            putExtra("payId",payId)
                            putExtra("txnTime",txnTime)
                            putExtra("operatorRef",operatorRef)
                            putExtra("statusDescription",statusDescription)
                            putExtra("message",message)
                            putExtra("billerName",billerName)
                            putExtra("providerName",providerName)
                            putExtra("amount",amount)
                            putExtra("type",type)
                            putExtra("recordId",recordId)
                            putExtra("operatorIcon", provider.providerImage)
                            putExtra("serviceName", toolbar.title.toString())
                            putExtra("number", strNumber)
                            putExtra("numberTitle", provider.dealerName)
                            putExtra("statusId", status.toInt())
                            putExtra("transactionType", AppConstants.BBPS)
                            putExtra(AppConstants.ORIGIN, AppConstants.TRANSACTION_ORIGIN)

                            startActivityForResult(this,0)
                        }

                    } else if (status == 200) {
                        val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else if (status == 300) {
                        val intent = Intent(this@BillPayment2Activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else {
                        val dialog: Dialog = AppDialogs.transactionStatus(this@BillPayment2Activity, message, 2)
                        dialog.show()
                        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                        btn_ok.setOnClickListener { view: View? -> dialog.dismiss() }
                    }
                } catch (e: JSONException) {
                    AppDialogs.volleyErrorDialog(this@BillPayment2Activity, 1)
                }
            },
            timeOutInSecond = 60

        )

    }

    private fun setupPayButtonText() = when (providerType) {
        ProviderType.POSTPAID -> "Pay Postpaid Bill"
        ProviderType.LOAN_REPAYMENT -> "Pay Loan Amount"
        ProviderType.GAS -> "Pay Gas Bill"
        ProviderType.ELECTRICITY -> "Pay Electricity Bill"
        ProviderType.INSURANCE -> "Pay Insurance Amount"
        else -> "Pay Bill"
    }

    private fun initViews() {
        //is pay without fetch bill
        if (provider.payWithoutFetch == 1) {
            binding.btnProceed.text = setupPayButtonText()
            binding.cvBbpsType.show()
            binding.tilAmount1.show()
        } else {
            binding.btnProceed.text = AppConstants.FETCH_BILL
            binding.tilAmount1.hide()
        }


        //is amount editable
        if (provider.isAmountEditable == 1) {
            binding.tilAmount2.show()
            binding.llAmountNonEditable.hide()
        } else {
            binding.tilAmount2.hide()
            binding.llAmountNonEditable.show()
        }

        //setup provider image
        binding.cvOperator1.setupPicassoImage(provider.providerImage)
        binding.cvOperator2.setupPicassoImage(provider.providerImage)

        //setup input type and title
        binding.edNumber.inputType = InputType.TYPE_CLASS_NUMBER


        if (provider.isAlphaNumeric == 1) {
            binding.edNumber.inputType = InputType.TYPE_CLASS_TEXT
            binding.edNumber.filters = arrayOf(InputFilter.AllCaps())
        }
        else if(provider.isAlphaNumeric == 0 && provider.isNumeric == 0){
            binding.edNumber.inputType = InputType.TYPE_CLASS_TEXT
            binding.edNumber.filters = arrayOf(InputFilter.AllCaps())
        }

        if (providerType == ProviderType.POSTPAID || providerType == ProviderType.MOBILE_PREPAID) {
            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = InputFilter.LengthFilter(10)
            binding.edNumber.filters = filterArray
        }

        if (!provider.dealerName.equals("", ignoreCase = true)
                && !provider.dealerName.equals("null", ignoreCase = true)) {
            binding.tilNumber.hint = "${provider.dealerName}"
            if (provider.minLength == 0 && provider.maxLength == 0)
                binding.tvNumberSubTitle.text = "Enter valid  ${provider.dealerName}"
            else if (provider.minLength == provider.maxLength)
                binding.tvNumberSubTitle.text = "Enter valid ${provider.maxLength} digits ${provider.dealerName}"
            else if (provider.minLength != 0 && provider.maxLength != 0)
                binding.tvNumberSubTitle.text = "Enter valid ${provider.minLength} to ${provider.maxLength} digits ${provider.dealerName}"

        } else {
            binding.tilNumber.hint = "Number"
            binding.tvNumberSubTitle.text = "Enter valid Number"
        }
    }

    private fun setupMinMaxLength() {
        //min - max length setup
        if (provider.minLength == 0 && provider.maxLength == 0) {
            binding.btnProceed.apply {
                isEnabled = true
                alpha = 1f
                isClickable = true
            }
            binding.btnProceed.apply {
                isEnabled = true
                alpha = 1f
                isClickable = false
            }
        } else if (provider.maxLength > 0) {
            val filterArray = arrayOfNulls<InputFilter>(2)
            binding.edNumber.apply {
                filterArray[0] = InputFilter.LengthFilter(provider.maxLength)
                filterArray[1] = InputFilter.AllCaps()
                filters = filterArray
            }
        }

        binding.edNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val mNumber: String = binding.edNumber.text.toString()
                if (provider.minLength == 0 && provider.maxLength > 0) {
                    if (provider.maxLength == mNumber.length) {
                        binding.btnProceed.apply {
                            isEnabled = true
                            alpha = 1f
                            isClickable = true
                        }
                    } else binding.btnProceed.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                } else if (provider.minLength > 0 && provider.maxLength == 0) {
                    if (mNumber.length >= provider.minLength) {
                        binding.btnProceed.apply {
                            isEnabled = true
                            isClickable = true
                            alpha = 1f
                        }
                    } else {
                        binding.btnProceed.apply {
                            isEnabled = false
                            isClickable = false
                            alpha = 0.5f
                        }
                    }
                } else if (provider.minLength > 0 && provider.maxLength > 0) {
                    if (mNumber.length >= provider.minLength && mNumber.length <= provider.maxLength) {
                        binding.btnProceed.apply {
                            isEnabled = true
                            isClickable = true
                            alpha = 1f
                        }
                    } else {
                        binding.btnProceed.apply {
                            isEnabled = false
                            isClickable = false
                            alpha = 0.5f
                        }

                    }
                } else {
                    binding.btnProceed.apply {
                        isEnabled = true
                        isClickable = true
                        alpha = 1f
                    }
                }
            }
        })
    }

    private fun setupToolbar() {

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        val serviceName = when (providerType) {
            ProviderType.BROADBAND -> AppConstants.BROADBAND
            ProviderType.MOBILE_PREPAID -> AppConstants.PREPAID
            ProviderType.DATA_CARD -> AppConstants.DATA_CARD
            ProviderType.DTH -> AppConstants.DTH
            ProviderType.ELECTRICITY -> AppConstants.ELECTRICITY
            ProviderType.POSTPAID -> AppConstants.POSTPAID
            ProviderType.WATER -> AppConstants.WATER
            ProviderType.GAS -> AppConstants.GAS
            ProviderType.LOAN_REPAYMENT -> AppConstants.LOAN_REPAYMENT
            ProviderType.FASTTAG -> AppConstants.FASTTAG
            ProviderType.INSURANCE -> AppConstants.INSURANCE
            else -> AppConstants.EMPTY
        }

        toolbar.title = serviceName
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }


        if (providerType == ProviderType.POSTPAID) binding.llMobileInput.hide()
        else binding.llMobileInput.show()

        //setup provider title
        binding.tvProviderName1.text = provider.providerName
        binding.tvProviderName2.text = provider.providerName


    }


    private fun setupExtraParams() {
        try {
            val extraParams = JSONArray(provider.extraParams)
            for (i in 0 until extraParams.length()) {
                val fieldName = extraParams.getJSONObject(i).getString("field_name")
                if (fieldName.equals("dob", ignoreCase = true)) {
                    binding.llDobInput.visibility = View.VISIBLE
                    isDobField = true
                }
                if (fieldName.equals("email", ignoreCase = true)) {
                    binding.llEmailInput.visibility = View.VISIBLE
                    isEmailField = true
                }
            }
        } catch (e: Exception) {
            showToast("${e.message.toString()}}")
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        AutoLogoutManager.cancelTimer()
        if (AutoLogoutManager.isSessionTimeout) {
            AutoLogoutManager.logout(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (AutoLogoutManager.isAppInBackground(this)) {
            AutoLogoutManager.startUserSession()
        }
    }


    private fun isValid(): Boolean {
        strAmount = if (provider.payWithoutFetch == 1)
            binding.edAmount1.text.toString()
        else {
            if (provider.isAmountEditable == 1)
                binding.edAmount2.text.toString()
            else binding.tvAmount2.text.toString()
        }

        strNumber = binding.edNumber.text.toString()
        strMobile = binding.edMobile.text.toString()
        strEmail = binding.edEmail.text.toString()
        strDob = binding.edDob.text.toString()

        if (strAmount.contains(".")) {
            try {
                val strPaisa = strAmount.substring(strAmount.indexOf(".") + 1)
                val intPaisa = strPaisa.toFloat().toInt()
                if (intPaisa == 0) strAmount = (strAmount.toFloat().toInt()).toString()
            } catch (e: Exception) {
            }
        }

        if (strAmount.isEmpty()) strAmount = "0"
        if (strNumber.isNotEmpty()) {
            if (strMobile.length == 10 || providerType == ProviderType.POSTPAID) {
                if (validateAmountInput()) {
                    if (isDobField || isEmailField) {
                        if (isDobField) {
                            if (strDob.length == 10) {
                                if (isEmailField) {
                                    if (strEmail.isNotEmpty()) {
                                        return validateProviderId()
                                    } else MakeToast.show(this, "Enter email id")
                                } else {
                                    return validateProviderId()
                                }
                            } else MakeToast.show(this, "Enter valid Dob")
                        } else {
                            if (strEmail.isNotEmpty()) {
                                if (isDobField) {
                                    if (strDob.length == 10) {
                                        return validateProviderId()
                                    } else MakeToast.show(this, "Enter valid Dob")
                                } else {
                                    return validateProviderId()
                                }
                            } else MakeToast.show(this, "Enter email id")
                        }

                    }
                    else {
                        return validateProviderId()
                    }
                }
            } else MakeToast.show(this, "Enter valid consumer mobile number")
        } else MakeToast.show(this, "Enter valid Number")
        return false
    }

    private fun validateAmountInput(): Boolean{


        if(binding.btnProceed.text.toString() == AppConstants.FETCH_BILL) return true

        val minAmount = provider.minAmount
        val maxAmount = provider.maxAmount



        if(minAmount == 0.0 && maxAmount == 0.0){
            if(strAmount.toDouble()  == 0.0){
                binding.tilAmount1.apply {
                    error = "Enter minimum amount 1 Rs."
                    isErrorEnabled = true
                }
                binding.tilAmount2.apply {
                    error = "Enter minimum amount 1 Rs."
                    isErrorEnabled = true
                }
                return false
            }else {
                binding.tilAmount1.isErrorEnabled = false
                binding.tilAmount2.isErrorEnabled = false
            }
        }


        if (minAmount == 0.0 && maxAmount > 0.0) {

            if (strAmount.toDouble() !in 1.0..maxAmount) {
                binding.tilAmount1.apply {
                    error = "Enter amount 1 to $maxAmount Rs."
                    isErrorEnabled = true
                }
                binding.tilAmount2.apply {
                    error = "Enter amount 1 to $maxAmount Rs."
                    isErrorEnabled = true
                }
                return false
            } else {
                binding.tilAmount1.isErrorEnabled = false
                binding.tilAmount2.isErrorEnabled = false
            }

        }


        if(minAmount >0.0 && maxAmount == 0.0) {

            if(strAmount.toDouble() < minAmount){
                binding.tilAmount1.apply {
                    error = "Enter minimum amount $minAmount."
                    isErrorEnabled = true
                }
                binding.tilAmount2.apply {
                    error = "Enter minimum amount $minAmount."
                    isErrorEnabled = true
                }
                return false
            }else {
                binding.tilAmount1.isErrorEnabled = false
                binding.tilAmount2.isErrorEnabled = false
            }

        }

        if(minAmount >0.0 && maxAmount >0.0){
            if(strAmount.toDouble() !in minAmount..maxAmount){

                binding.tilAmount1.apply {
                    error = provider.amountMessage
                    isErrorEnabled = true
                }
                binding.tilAmount2.apply {
                    error = provider.amountMessage
                    isErrorEnabled = true
                }
                return false
            }
            else {
                binding.tilAmount1.isErrorEnabled = false
                binding.tilAmount2.isErrorEnabled = false
            }
        }


        return true
    }

    private fun validateProviderId(): Boolean {
        if (provider.id === "161") {
            if (strNumber.startsWith("3")) {
                return true
            } else MakeToast.show(this, "please make sure, Electricity K-Number start with 3")
        } else if (provider.id === "160") {
            if (strNumber.startsWith("2")) {
                return true
            } else MakeToast.show(this, "please make sure, Electricity K-Number start with 2")
        } else {
            return true
        }

        return false
    }

    override fun onBackPressed() {

        if (binding.btnProceed.text.toString() == AppConstants.PAY_BILL
            && provider.payWithoutFetch == 1)
            super.onBackPressed()

        if (binding.btnProceed.text.toString() == AppConstants.PAY_BILL) {
            binding.cvTwo.hide()
            binding.cvBbpsType.hide()
            binding.cvOne.show()
            binding.btnProceed.text = AppConstants.FETCH_BILL
            fetchAmount = AppConstants.EMPTY
        } else super.onBackPressed()
    }

    private fun setupAutoCompleteView() {
        val animalList = getAnimalList()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, R.layout.spinner_layout,
                animalList)
        binding.actBbpsType.setAdapter(adapter)
        binding.actBbpsType.onItemClickListener =
                AdapterView.OnItemClickListener { parent, arg1, position, id ->
                    binding.tilBbpsType.hint = "BPPS Type"
                    bbpsType = parent.getItemAtPosition(position).toString()
                }

    }

    private fun getAnimalList(): ArrayList<String> {
        val animalList = ArrayList<String>()
        animalList.add(AppConstants.BBPS_ONE)
        animalList.add(AppConstants.BBPS_TWO)
        return animalList
    }


    private fun fetchUserBalance(){
        WebApiCall.getRequestWithHeader(this, APIs.USER_BALANCE)
        WebApiCall.webApiCallback(object  : WebApiCallListener {
            override fun onFailure(message: String) {

            }
            override fun onSuccessResponse(jsonObject: JSONObject) {

                val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")

                if(status ==1){
                    val detail = jsonObject.getJSONObject("details")
                    val userBalance =  detail.getString("user_balance")
                    appPreference.userBalance= userBalance
                    binding.tvAmountText.text = "Wallet available balance: ₹$userBalance"
                }



            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        if(location != null){
            onFetch(location)
        }
        else {
            requestForLocationPhoneReadState {
                val isLocationServiceEnable = LocationService.isLocationEnabled(this)
                if (isLocationServiceEnable) {
                    onStart()
                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            if (this@BillPayment2Activity.location == null) {
                                this@BillPayment2Activity.location = location
                                onFetch(location)
                            }

                        }
                    })
                }
            }
        }
    }

}



