package com.a2zsuvidhaa.`in`.activity.recharge

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.activity.login.globalLocation
import com.a2zsuvidhaa.`in`.databinding.ActivityRecharge2Binding
import com.a2zsuvidhaa.`in`.listener.OfferCallbackListener
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.Offer
import com.a2zsuvidhaa.`in`.model.Provider
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.ProviderType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class Recharge2Activity : AppCompatActivity(), OfferCallbackListener {

    @Inject
    lateinit var volleyClient: VolleyClient

    private lateinit var binding: ActivityRecharge2Binding
    private var amount: String = AppConstants.EMPTY
    private var number: String = AppConstants.EMPTY
    private var minAmount: Double = 10.0
    private var maxLength: Int = 14
    private var numberStartWith: Int = -1
    private var shouldCountMaxLength = true
    private var isNumberInputError = false

    private var offerList: ArrayList<Offer> = ArrayList()

    private var progressDialog: Dialog? = null
    private var offerListDialog: Dialog? = null

    private lateinit var provider: Provider
    private lateinit var providerType: ProviderType
    private lateinit var appPreference: AppPreference
    private lateinit var toolbar: Toolbar

    private val appViewModel: AppViewModel by viewModels()

    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var location: Location? = globalLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecharge2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreference = AppPreference.getInstance(this)
        providerType = intent.getSerializableExtra(ProviderActivity.PROVIDER_TYPE) as ProviderType
        provider = intent.getParcelableExtra(ProviderActivity.PROVIDER)!!

        setupView()
        editTextChangeListener()

        binding.tvAmountText.text =
            "Wallet available balance: ₹${AppPreference.getInstance(this).userBalance}"

        binding.btnInfoOffer.setOnClickListener {
            if (providerType == ProviderType.DTH)
                fetchCustomerInfo()
            else {
                if (offerList.isEmpty()) fetchROffer()
                else showOfferListDialog()
            }
        }

        binding.btnProceed.setOnClickListener {

            try {
                if (amount.toDouble() <= appPreference.userBalance.toDouble()) {

                    fetchLocation(onStart = {
                        progressDialog = StatusDialog.progress(this, "Validating Location")
                    }) {
                        progressDialog?.dismiss()
                        dialogConfirmRecharge()
                    }
                } else {
                    Snackbar.make(
                        binding.coordinatorLayout,
                        "Insufficient wallace balance",
                        Snackbar.LENGTH_LONG
                    ).apply {
                        setBackgroundTint(
                            ContextCompat.getColor(
                                this@Recharge2Activity,
                                R.color.red
                            )
                        )
                        show()
                    }
                } //showToast("Transaction can't proceed due to enough balance")
            } catch (e: Exception) {
                showToast("Something went wrong, please contact with admin")
            }
        }

        fetchUserBalance()

        fetchLocation()

        binding.llRefresh.setOnClickListener {
            appViewModel.fetchBalanceInfo()
        }

        subscribers()
    }

    private fun dialogConfirmRecharge() {

        Dialogs.confirmRecharge(
            this,
            operator = provider.providerName,
            number = number,
            amount = amount
        )
            .apply {
                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    confirmAndRecharge()
                }
            }
    }

    private fun subscribers() {
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

    private fun confirmAndRecharge() {
        val url = APIs.RECHARGE
        progressDialog = StatusDialog.fullScreenProgress(this)

        val param = HashMap<String, String>()
        param["number"] = number
        param["provider"] = provider.id
        param["amount"] = amount




        volleyClient.postRequest(
            url, param, onSuccess = {jsonObject->
                progressDialog?.dismiss()
                try {
                    val status = jsonObject.getString("status")
                    val message = jsonObject.getString("message")
                    if (status.equals("1", ignoreCase = true)
                        || status.equals("2", ignoreCase = true)
                        || status.equals("3", ignoreCase = true)
                    ) {

                        val statusDescription = jsonObject.optString("statusDescription")
                        val payId = jsonObject.optString("payid")
                        val operatorRef = jsonObject.optString("operator_ref")
                        val txnTime = jsonObject.optString("txnTime")
                        val operator = jsonObject.optString("operator")
                        val amount = jsonObject.optString("amount")
                        val recordId = jsonObject.optString("recordId")


                        val numberTitle = if (provider.dealerName == "null"
                            || provider.dealerName == "nul"
                        ) "Number" else provider.dealerName

                        Intent(
                            this@Recharge2Activity,
                            BillRechargeResultActivity::class.java
                        ).apply {
                            putExtra("payId", payId)
                            putExtra("txnTime", txnTime)
                            putExtra("operatorRef", operatorRef)
                            putExtra("statusDescription", statusDescription)
                            putExtra("message", message)
                            putExtra("billerName", AppConstants.EMPTY)
                            putExtra("providerName", operator)
                            putExtra("amount", amount)
                            putExtra("recordId", recordId)
                            putExtra("operatorIcon", provider.providerImage)
                            putExtra("serviceName", toolbar.title.toString())
                            putExtra("number", number)
                            putExtra("numberTitle", numberTitle)
                            putExtra("statusId", status.toInt())
                            putExtra("transactionType", AppConstants.MOBILE_RECHARGE)
                            putExtra(AppConstants.ORIGIN, AppConstants.TRANSACTION_ORIGIN)

                            startActivityForResult(this, 0)
                        }


                    } else if (status.equals("200", ignoreCase = true)) {
                        val intent =
                            Intent(this@Recharge2Activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else if (status.equals("300", ignoreCase = true)) {
                        val intent =
                            Intent(this@Recharge2Activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else {
                        val dialog =
                            AppDialogs.transactionStatus(this@Recharge2Activity, message, 2)
                        dialog.show()
                        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                        btn_ok.setOnClickListener { view: View? -> dialog.dismiss() }
                    }
                } catch (e: JSONException) {
                    AppLog.d("Recharge Test : ${e.message}")
                    AppDialogs.volleyErrorDialog(this@Recharge2Activity, 1)
                }
            }, onFailure = {
                progressDialog?.dismiss()
                AppDialogs.volleyErrorDialog(this@Recharge2Activity, 0)
            },
            timeOutInSecond = 60
        )

    }

    private fun fetchROffer() {
        if (providerType != ProviderType.MOBILE_PREPAID) return@fetchROffer

        val url = APIs.GET_SPECIAL_OFFER


        val param = hashMapOf(
            "mobile_number" to number,
            "provider" to provider.providerName
        )

        volleyClient.getRequest(url, param, onSuccess = { jsonObject ->

            val status = jsonObject.optString("status")
            if (status.equals("SUCCESS", true)) {
                offerList.clear()
                val response = jsonObject.getJSONArray("Response")
                for (i in 0 until response.length()) {
                    val offerObject = response.getJSONObject(i)

                    val offer = Offer(
                        price = offerObject.optString("price"),
                        offerDesc = offerObject.optString("offer"),
                        /*commAmount =  offerObject.optString("commAmount"),
                        commType =  offerObject.optString("commType"),*/
                    )
                    offerList.add(offer)
                }

            }

        }, onFailure = {}, tag = RequestTag.CANCEL_REQUEST)


    }

    private fun showOfferListDialog() {


        val offerAdapter = OfferAdapter(offerList, this)
        offerAdapter.setupOfferCallbackListener(this)

        offerListDialog = Dialogs.rOfferListDialog(this).apply {
            findViewById<RecyclerView>(R.id.recycler_view).apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(this@Recharge2Activity)
                adapter = offerAdapter
            }
        }

    }

    private fun fetchCustomerInfo() {
        progressDialog = StatusDialog.progress(this, "Fetching Customer Info...")
        binding.llCustomerInfo.hide()
        val url: String = (APIs.GET_DTH_INFO + "?acc_no=" + number + "&provider_id=" + provider.id)
        WebApiCall.getRequest(this, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String?) {
                progressDialog?.dismiss()
                MakeToast.show(this@Recharge2Activity, "Server Error")
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                try {
                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")
                    if (status == 1) {

                        val record = jsonObject.getJSONObject("planInfo")

                        val balance = record.optString("Balance")
                        val customerName = record.optString("customerName")
                        val nextRechargeDate = record.optString("NextRechargeDate")
                        val activeStatus = record.optString("status")
                        val monthlyRecharge = record.optString("MonthlyRecharge")


                        binding.let {
                            it.llCustomerInfo.show()
                            it.tvNextRechargeDate.text = nextRechargeDate
                            it.tvCustomerName.text = customerName
                            it.tvActiveStatus.text = activeStatus
                            it.tvMonthlyRecharge.text = monthlyRecharge
                            it.tvBalance.text = balance
                        }


                    } else MakeToast.show(
                        this@Recharge2Activity,
                        message.ifEmpty { "Unable to fetch bill information" })
                } catch (e: Exception) {
                    MakeToast.show(
                        this@Recharge2Activity,
                        e.message?.ifEmpty { "Unable to fetch bill information" })
                }
            }

        })

    }

    private fun setupView() {


        toolbar = findViewById<Toolbar>(R.id.toolbar)


        binding.cvOperator.setupPicassoImage(provider.providerImage)
        binding.tvProviderName.text = provider.providerName

        binding.btnInfoOffer.text =
            if (providerType == ProviderType.DTH) "Customer Info" else "R Offer"

        val filterArray = arrayOfNulls<InputFilter>(1)
        when (providerType) {
            ProviderType.MOBILE_PREPAID -> {
                toolbar.title = AppConstants.PREPAID
                maxLength = 10
                numberStartWith = -1
                filterArray[0] = LengthFilter(maxLength)
                binding.edNumber.filters = filterArray
                binding.tilNumber.hint = "Mobile Number"
                binding.tvNumberSubTitle.text = "Enter 10 Digit Mobile Number."

            }
            ProviderType.DTH -> {
                toolbar.title = AppConstants.DTH
                binding.tilNumber.hint = "Enter DTH number"
                binding.tvNumberSubTitle.text = "Enter valid DTH Number."

                when {
                    provider.id.equals("12", ignoreCase = true) -> {
                        maxLength = 11
                        numberStartWith = 0
                        filterArray[0] = LengthFilter(maxLength)
                        binding.edNumber.filters = filterArray
                        binding.tvNumberSubTitle.text = "Number start with 0 and is 11 digits long"
                        minAmount = 100.0
                        binding.tvMinAmount.text = "Minimum Amount: ₹100"
                    }
                    provider.id.equals("13", ignoreCase = true) -> {
                        maxLength = 10
                        numberStartWith = 1
                        binding.tvNumberSubTitle.text = "Number start with 1 and is 10 digits long"
                        minAmount = 50.0
                        binding.tvMinAmount.text = "Minimum Amount: ₹ 50"

                        filterArray[0] = LengthFilter(maxLength)
                        binding.edNumber.filters = filterArray
                    }
                    provider.id.equals("17", ignoreCase = true) -> {
                        maxLength = 10
                        numberStartWith = 3
                        binding.tvNumberSubTitle.text = "Number start with 3 and is 10 digits long"
                        minAmount = 100.0
                        binding.tvMinAmount.text = "Minimum Amount: ₹ 100"

                        filterArray[0] = LengthFilter(maxLength)
                        binding.edNumber.filters = filterArray
                    }
                    // provider.id.equals("15", ignoreCase = true)
                    else -> {
                        numberStartWith = -1
                        minAmount = 100.0
                        binding.tvMinAmount.text = "Minimum Amount: ₹ 100"
                        shouldCountMaxLength = false
                    }
                }

            }
            else -> {
            }

        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }


    }

    private fun editTextChangeListener() {

        numberChangeListener()

        amountChangeListener()
    }

    private fun numberChangeListener() {


        binding.edNumber.afterTextChange {

            //initial setup
            number = it
            amount = binding.edAmount.text.toString()


            if (amount.isEmpty()) amount = AppConstants.ZERO_STRING
            val doubleAmount = amount.toDouble()

            //text input layout amount error setup
            binding.tilNumber.apply {
                if (numberStartWith != -1) {
                    if (!it.startsWith(numberStartWith.toString())) {
                        isErrorEnabled = true
                        error = "Number should start with $numberStartWith"
                        isNumberInputError = true
                    } else {
                        binding.btnInfoOffer.hide()
                        isErrorEnabled = false
                        isNumberInputError = false
                    }
                }
            }


            // fetch customer button visibility for DTH and Prepaid
            if (!isNumberInputError) {
                if (it.length == maxLength || !shouldCountMaxLength) {
                    binding.btnInfoOffer.show()
                } else binding.btnInfoOffer.hide()
            }

            if (number.length == 10) fetchROffer()


            // setup proceed button visibility
            binding.btnProceed.apply {
                if ((it.length == maxLength || !shouldCountMaxLength) && !isNumberInputError && doubleAmount >= minAmount) this.setupVisibility()
                else this.setupVisibility(false)
            }
        }
    }

    private fun amountChangeListener() {
        binding.edAmount.afterTextChange {


            //initial setup
            number = binding.edNumber.text.toString()
            amount = it
            if (amount.isEmpty()) amount = AppConstants.ZERO_STRING
            val doubleAmount = amount.toDouble()


            // text input layout error setup
            binding.tilAmount.apply {
                if (minAmount > doubleAmount) {
                    isErrorEnabled = true
                    error = "Min Amount $minAmount"
                } else {
                    isErrorEnabled = false
                }
            }

            //proceed button visibility setup
            binding.btnProceed.apply {
                if ((number.length == maxLength || !shouldCountMaxLength) && !isNumberInputError && doubleAmount >= minAmount) this.setupVisibility()
                else this.setupVisibility(false)
            }

            //ROffer filter out via amount change
            val offer = offerList.find { offer ->
                offer.price == amount
            }
            offer?.let {
                binding.run {
                    this.llOffer.show()
                    this.tvOfferPrice.text = it.price
                    this.tvOfferDescription.text = it.offerDesc
                }
            } ?: binding.llOffer.hide()
        }
    }

    override fun onOfferCallback(offer: Offer?) {
        offerListDialog?.dismiss()
        offer?.let {
            binding.edAmount.setText(offer.price)
        }
    }

    private fun fetchUserBalance() {
        WebApiCall.getRequestWithHeader(this, APIs.USER_BALANCE)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String) {

            }

            override fun onSuccessResponse(jsonObject: JSONObject) {

                val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")

                if (status == 1) {
                    val detail = jsonObject.getJSONObject("details")
                    val userBalance = detail.getString("user_balance")
                    appPreference.userBalance = userBalance
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
        if (location != null) {
            onFetch(location)
        } else {
            requestForLocationPhoneReadState {
                val isLocationServiceEnable = LocationService.isLocationEnabled(this)
                if (isLocationServiceEnable) {
                    onStart()
                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            if (this@Recharge2Activity.location == null) {
                                this@Recharge2Activity.location = location
                                onFetch(location)
                            }

                        }
                    })
                }
            }
        }
    }
}