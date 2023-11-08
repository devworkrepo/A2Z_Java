package com.a2zsuvidhaa.`in`.adapter.report

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BaseRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.model.report.CommissionSchemeList
import com.a2zsuvidhaa.`in`.databinding.ListCommissionSchemeBinding

class SchemeListAdapter() : BaseRecyclerViewAdapter<CommissionSchemeList, ListCommissionSchemeBinding>(R.layout.list_commission_scheme) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListCommissionSchemeBinding>, position: Int) {

        val item = items[position]
        val binding = holder.binding

        binding.tvTitle.text = item.title

        binding.rootLayout.setOnClickListener {
            onItemClick?.invoke(it, item, position)
        }

    }
}