package com.a2zsuvidhaa.`in`.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.activity.AppInProgressActivity
import com.a2zsuvidhaa.`in`.activity.ShowQRImageActivity
import com.a2zsuvidhaa.`in`.activity.fund_request.FundRequestActivity
import com.a2zsuvidhaa.`in`.adapter.AgentRequestViewAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentAgentRequestViewBinding
import com.a2zsuvidhaa.`in`.fragment.home.HomeViewModel
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.AgentRequestView
import com.a2zsuvidhaa.`in`.model.Remark
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.launchIntent
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AgentRequestViewFragment2() : BaseFragment<FragmentAgentRequestViewBinding>(R.layout.fragment_agent_request_view) {

    @Inject
    lateinit var appPreference: AppPreference
    @Inject
    lateinit var security : Security

    private val viewModel: HomeViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.tvNumber.text = security.decrypt(appPreference.mobile)

        setupOnClickListener()

        getAgentRequestViewList()

        viewModel.fetchBalanceInfo()
        subscriberObservers()
    }

    private fun subscriberObservers(){
        viewModel.balanceObs.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.run {

                        llBalance.hide()
                        tvViewBalance.hide()
                        tvAmountProgressBar.show()
                    }
                }
                is Resource.Success -> {
                    binding.run {
                        tvAmount.text = it.data.balance.userBalance
                        llBalance.show()
                        tvAmountProgressBar.hide()

                    }
                }
                is Resource.Failure -> {
                    binding.run {
                        llBalance.hide()
                        tvViewBalance.show()
                        tvAmountProgressBar.hide()
                    }
                }
            }
        })
    }


    private fun setupOnClickListener() {
        binding.let {
            it.llTopUpWallet.setOnClickListener {
             /*   MainActivity.spos = 0
                (activity as MainActivity?)?.replaceFragment(BankDetailFragment.newInstance())*/
                activity?.launchIntent(FundRequestActivity::class.java, bundleOf(AppConstants.ORIGIN to "topup"))
            }

            it.tvViewBalance.setOnClickListener{
                viewModel.fetchBalanceInfo()
            }
            it.llBalance.setOnClickListener {
                viewModel.fetchBalanceInfo()
            }

            it.llQrCode.setOnClickListener {
                context?.launchIntent(ShowQRImageActivity::class.java)
            }


        }
    }

    private fun getAgentRequestViewList(){

        val url: String = APIs.AGENT_REQUEST_VIEW+"?"
        binding.progressBar.show()
        
        WebApiCall.getRequest(requireContext(),url)
        WebApiCall.webApiCallback(object  : WebApiCallListener {
            override fun onFailure(message: String) {
               binding.progressBar.hide()
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                binding.progressBar.hide()

                try {
                  
                    val status: String =jsonObject.getString("status")
                    if (status.equals("1", ignoreCase = true)) {
                        val count: Int =jsonObject.getInt("count")
                        if (count > 0) {
                            val remarkObject: JSONObject =jsonObject.getJSONObject("remarks")
                            val iter: Iterator<*> = remarkObject.keys()
                            val remarks: ArrayList<Remark> = ArrayList<Remark>()
                            while (iter.hasNext()) {
                                val key = iter.next().toString()
                                val value = remarkObject.getString(key)
                                val remark = Remark(key, value)
                                remarks.add(remark)
                            }
                            val list: ArrayList<AgentRequestView> = ArrayList<AgentRequestView>()
                            val resultArray: JSONArray =jsonObject.getJSONArray("result")
                            for (i in 0 until resultArray.length()) {
                                val jsonObject2 = resultArray.getJSONObject(i)
                                val createdAt = jsonObject2.getString("created_at")
                                val id = jsonObject2.getString("id")
                                val userId = jsonObject2.getString("user_id")
                                val userName = jsonObject2.getString("user_name")
                                val firmName = jsonObject2.getString("firm_name")
                                val role = jsonObject2.getString("role")
                                val mobile = jsonObject2.getString("mobile")
                                val mode = jsonObject2.getString("mode")
                                val branchCode = jsonObject2.getString("branch_code")
                                val onlinePaymentMode = jsonObject2.getString("online_payment_mode")
                                val depositDate = jsonObject2.getString("deposit_date")
                                val bankName = jsonObject2.getString("bank_name")
                                val remark = jsonObject2.getString("remark")
                                val slip = jsonObject2.getString("slip")
                                val refId = jsonObject2.getString("ref_id")
                                val amount = jsonObject2.getString("amount")
                                val status1 = jsonObject2.getString("status")
                                val statusId = jsonObject2.getString("status_id")
                                val agentRequestView = AgentRequestView(
                                        createdAt, id, userId, userName, firmName, role, mobile,
                                        mode, branchCode, onlinePaymentMode, depositDate,
                                        bankName, remark, slip, refId, amount, status1, statusId,
                                        remarks
                                )
                                list.add(agentRequestView)
                            }
                           val  adapter = AgentRequestViewAdapter(activity, list)
                            binding.recyclerView.setHasFixedSize(false)
                            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            binding.recyclerView.adapter = adapter
                            adapter.setupOnClickListener { requireActivity().recreate() }
                            if (adapter.itemCount > 0) {
                                binding.tvNoResult.visibility = View.GONE
                            } else {
                                binding.tvNoResult.visibility = View.VISIBLE
                            }
                        } else {
                            binding.tvNoResult.visibility = View.VISIBLE
                        }
                    } else if (status.equals("200", ignoreCase = true)) {
                        val message: String =jsonObject.getString("message")
                        val intent = Intent(activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 0)
                        startActivity(intent)
                    } else if (status.equals("300", ignoreCase = true)) {
                        val message: String =jsonObject.getString("message")
                        val intent = Intent(activity, AppInProgressActivity::class.java)
                        intent.putExtra("message", message)
                        intent.putExtra("type", 1)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    activity?.showToast(e.message.toString())
                }
                
            }

        })
    }

    companion object {
        fun newInstance() = AgentRequestViewFragment2()
    }
}