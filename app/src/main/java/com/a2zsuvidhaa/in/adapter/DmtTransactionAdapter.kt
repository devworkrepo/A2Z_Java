package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.DmtTransactionDetail
import com.a2zsuvidhaa.`in`.databinding.ListDmtResponseBinding


class DmtTransactionAdapter() :
    BaseRecyclerViewAdapter<DmtTransactionDetail, ListDmtResponseBinding>(R.layout.list_dmt_response) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListDmtResponseBinding>,
        position: Int
    ) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item
    }
}