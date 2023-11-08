package com.a2zsuvidhaa.`in`.activity.fund_request

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.FragmentFundRequestTypeBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.enums.FundRequestType

class FundRequestTypeFragment() :
        BaseFragment<FragmentFundRequestTypeBinding>(R.layout.fragment_fund_request_type)
{


    interface  OnFundRequestTypeFragmentItemClickListener{
        fun onFundRequestTypeFragmentItemClick(type : FundRequestType)
    }



    private var listener : OnFundRequestTypeFragmentItemClickListener ? = null
    private lateinit var toolbar : Toolbar


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FundRequestActivity
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)

        binding.run {
            cvUpi.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.UPI_PAYMENT)}
            cvPaymentGateway.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.PAYMENT_GATEWAY)}
            cvBankTransfer.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.BANK_TRANSFER)}
            cvCashCollect.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.CASH_COLLECT)}
            cvCashDepositCounter.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.CASH_DEPOSIT_COUNTER)}
            cvCashDepositMachine.setOnClickListener{listener?.onFundRequestTypeFragmentItemClick(FundRequestType.CASH_DEPOSIT_MACHINE)}
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar(toolbar,"Fund Request")

    }

    companion object{
        fun newInstance() = FundRequestTypeFragment()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}