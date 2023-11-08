package com.a2zsuvidhaa.`in`.adapter.report

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BaseRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.model.report.CommissionSchemeDetail

import com.a2zsuvidhaa.`in`.databinding.ListCommissionSchemeDetailBinding
import com.a2zsuvidhaa.`in`.util.ents.hide

class SchemeDetailAdapter() : BaseRecyclerViewAdapter<CommissionSchemeDetail, ListCommissionSchemeDetailBinding>(R.layout.list_commission_scheme_detail) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListCommissionSchemeDetailBinding>, position: Int) {



        val item = items[position]
        val binding = holder.binding

        binding.tvAgentCharge.text = item.agentCharge
        binding.tvAgentCommission.text = item.agentCommission
        binding.tvCategory.text = item.category
        binding.tvMaxAmount.text = item.maxAmount
        binding.tvMinAmount.text = item.minAmount
        binding.tvOperator.text = item.operator
        binding.tvService.text = item.service

        if(item.agentCharge.orEmpty().isEmpty()) binding.llAgentCharge.hide()
        if(item.agentCommission.orEmpty().isEmpty()) binding.llAgentCommission.hide()
        if(item.category.orEmpty().isEmpty()) binding.llCategory.hide()
        if(item.operator.orEmpty().isEmpty()) binding.llOperator.hide()
        if(item.service.orEmpty().isEmpty()) binding.llService.hide()
        if(item.minAmount.orEmpty().isEmpty()) binding.llMinAmount.hide()
        if(item.maxAmount.orEmpty().isEmpty()) binding.llMaxAmount.hide()
    }

}