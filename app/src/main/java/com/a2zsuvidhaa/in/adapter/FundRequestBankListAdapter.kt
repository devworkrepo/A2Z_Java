package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListFundRequestBankBinding
import com.a2zsuvidhaa.`in`.model.BankDetail

class FundRequestBankListAdapter() : BaseRecyclerViewAdapter<BankDetail, ListFundRequestBankBinding>
(R.layout.list_fund_request_bank) {

    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListFundRequestBankBinding>, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.item = item

        binding.rootLayout.setOnClickListener {
            onItemClick?.invoke(it,item,position)
        }
    }
}