package com.a2zsuvidhaa.`in`.activity.recharge

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.adapter.ProviderAdapter
import com.a2zsuvidhaa.`in`.databinding.ActivityOperatorProviderBinding
import com.a2zsuvidhaa.`in`.fragment.home.HomeFragment
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.Provider
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.enums.ProviderType
import com.a2zsuvidhaa.`in`.util.ents.afterTextChange
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ProviderActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityOperatorProviderBinding

    var requestType: String? = null
    private var adapter: ProviderAdapter? = null
    private var providerType: ProviderType? = null
    private val stateListsHashMap = HashMap<String, String>()
    private var stateId: String? = ""
    private lateinit var providerList: ArrayList<Provider>
    private var defaultBillProviderIcon: Int = 0
    private var spinnerDialog: SpinnerDialog? = null
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOperatorProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        providerType = intent.getSerializableExtra(HomeFragment.PROVIDER_TYPE) as ProviderType?
        sessionManager = SessionManager(this@ProviderActivity)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupTitle()
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }




        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProviderActivity)
            setHasFixedSize(false)
        }


        providers()
        binding.btnRefresh.setOnClickListener { providers() }

        binding.edSearch.afterTextChange {
            filter(it)
        }
        binding.llState.setOnClickListener {
            spinnerDialog?.showSpinerDialog()
        }

        if (providerType == ProviderType.MOBILE_PREPAID
                || providerType == ProviderType.POSTPAID
                || providerType == ProviderType.DTH)
            binding.edSearch.hide()
    }

    private fun setupTitle() {
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

        toolbar.title = "Select $serviceName Provider"

    }

    private fun filter(text: String) {
        adapter?.let {
            val filteredList: ArrayList<Provider> = ArrayList()
            for (item in providerList) {
                if (item.providerName.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
            }
            it.filterList(filteredList)
        }
    }

    var sessionManager: SessionManager? = null
    private fun providers() {


        requestType = "MOBILE_PREPAID"
        when (providerType) {
            ProviderType.MOBILE_PREPAID -> {
                requestType = "MOBILE_PREPAID"
                defaultBillProviderIcon = R.drawable.ic_launcher_mobile
            }
            ProviderType.DTH -> {
                requestType = "DTH"
                defaultBillProviderIcon = R.drawable.ic_launcher_dth
            }
            ProviderType.DATA_CARD -> {
                requestType = "DATA_CARD"
                defaultBillProviderIcon = R.drawable.ic_launcher_mobile
            }
            ProviderType.POSTPAID -> {
                requestType = "POSTPAID"
                defaultBillProviderIcon = R.drawable.ic_launcher_mobile
            }
            ProviderType.BROADBAND -> {
                requestType = "BROADBAND"
                defaultBillProviderIcon = R.drawable.ic_launcher_broadband
            }
            ProviderType.LANDLINE -> {
                requestType = "LANDLINE"
                defaultBillProviderIcon = R.drawable.icon_landline
            }
            ProviderType.WATER -> {
                requestType = "WATER"
                defaultBillProviderIcon = R.drawable.ic_launcher_water
            }
            ProviderType.GAS -> {
                requestType = "GAS"
                defaultBillProviderIcon = R.drawable.ic_launcher_gas
            }
            ProviderType.INSURANCE -> {
                requestType = "INSURANCE"
                defaultBillProviderIcon = R.drawable.ic_launcher_insurence
            }
            ProviderType.ELECTRICITY -> {
                binding.llState.show()
                requestType = "ELECTRICITY"
                defaultBillProviderIcon = R.drawable.ic_launcher_electricity
            }
            ProviderType.FASTTAG -> {
                requestType = "FASTTAG"
                defaultBillProviderIcon = R.drawable.fastag
            }
            ProviderType.LOAN_REPAYMENT -> {
                requestType = "LOAN_REPAYMENT"
                defaultBillProviderIcon = R.drawable.ic_launcher_insurence
            }
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.llSomething.visibility = View.GONE
        binding.cvContent.visibility = View.GONE


        val url = "${APIs.GET_RECHARGE_PROVIDER}?state_id=${stateId}&requestType=${requestType}"
        WebApiCall.getRequest(this, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                providerList = ArrayList()
                val status: Int = jsonObject.getInt("status")
                if (status == 1) {
                    val baseUrl: String = jsonObject.getString("baseUrl")
                    val serviceId: String = jsonObject.getString("serviceId")
                    val array: JSONArray = jsonObject.getJSONArray("provider")
                    for (i in 0 until array.length()) {
                        val providerObject: JSONObject = array.getJSONObject(i)
                        val provider = Provider(
                                defaultIcon = defaultBillProviderIcon,
                                id = providerObject.getString("id"),
                                providerName = providerObject.getString("provider_name"),
                                providerImage = baseUrl + providerObject.getString("provider_image"),
                                serviceId = serviceId,
                                dealerName = providerObject.getString("dealer_name"),
                                extraParams = providerObject.getJSONArray("extraparam").toString(),
                                isAmountEditable = providerObject.getInt("is_amount_editable"),
                                payWithoutFetch = providerObject.getInt("pay_without_fetch"),
                                isNumeric = providerObject.getInt("is_numeric"),
                                isAlphaNumeric = providerObject.getInt("is_alpha_numeric"),
                                minLength = providerObject.getInt("min_length"),
                                maxLength = providerObject.getInt("max_length"),
                                minAmount = providerObject.getDouble("min_amount"),
                                maxAmount = providerObject.getDouble("max_amount"),
                                amountMessage = providerObject.optString("message_amount"),
                        )
                        providerList.add(provider)
                    }
                    if (providerType == ProviderType.ELECTRICITY) {

                        if (jsonObject.has("stateList")) {
                            binding.cvContent.visibility = View.VISIBLE
                            val stateObject: JSONObject = jsonObject.getJSONObject("stateList")
                            parseStateList(stateObject)
                        }
                    }
                    binding.cvContent.visibility = View.VISIBLE
                    sortArrayList(providerList)
                    adapter = ProviderAdapter(this@ProviderActivity, providerList)
                    binding.recyclerView.adapter = adapter
                    adapter!!.setupProviderCallback { provider: Provider? ->
                        val intent = if (sessionManager!!.getString("bbps").equals("1", ignoreCase = true)) {
                            Intent(this@ProviderActivity, Recharge2Activity::class.java)
                        } else {
                            Intent(this@ProviderActivity, BillPayment2Activity::class.java)
                        }
                        intent.putExtra(PROVIDER_TYPE, providerType)
                        intent.putExtra("requestType", requestType)
                        intent.putExtra(PROVIDER, provider)
                        startActivity(intent)
                    }
                    if (adapter!!.itemCount == 0) MakeToast.show(this@ProviderActivity, "No provider found for this circle")
                } else if (status == 200) {
                    val intent: Intent = Intent(this@ProviderActivity, AppInProgressActivity::class.java)
                    val message: String = jsonObject.getString("message")
                    intent.putExtra("message", message)
                    intent.putExtra("type", 0)
                    startActivity(intent)
                } else if (status == 300) {
                    val intent: Intent = Intent(this@ProviderActivity, AppInProgressActivity::class.java)
                    val message: String = jsonObject.getString("message")
                    intent.putExtra("message", message)
                    intent.putExtra("type", 1)
                    startActivity(intent)
                }
                binding.progressBar.visibility = View.GONE
                binding.llSomething.visibility = View.GONE
            }

            override fun onFailure(message: String) {
                binding.progressBar.visibility = View.GONE
                binding.llSomething.visibility = View.VISIBLE
            }
        })

    }

    private fun parseStateList(stateObject: JSONObject) {
        try {
            stateListsHashMap.clear()
            val iterator: Iterator<*> = stateObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                val value = stateObject.getString(key)
                stateListsHashMap[value] = key
            }


            binding.tvState.text = "RAJASTHAN"
            val sortedMap = Utils.sortByComparator(stateListsHashMap, true) as HashMap<String, String>
            val list = ArrayList(sortedMap.keys)
            spinnerDialog = SpinnerDialog(this, list, "Search State", "Close")
            spinnerDialog?.let { spinner ->
                spinner.setCancellable(true); // for cancellable
                spinner.setShowKeyboard(false);// for open keyboard by default
                spinner.bindOnSpinerListener { item, position ->

                    stateId = sortedMap[item]
                    binding.tvState.text = item
                    providers()
                }
            }
        } catch (e: JSONException) {
            MakeToast.show(this, "exception")
            e.printStackTrace()
        }
    }

    companion object {
        const val PROVIDER_TYPE = "provider_type"
        const val PROVIDER = "provider"
    }

    private fun sortArrayList(providerList: ArrayList<Provider>) {

        providerList.sortWith(comparator = { o1, o2 ->
            o1.providerName.compareTo(o2.providerName)
        })

        //providerList.sortWith { o1, o2 -> o1.providerName.compareTo(o2.providerName) }
    }
}