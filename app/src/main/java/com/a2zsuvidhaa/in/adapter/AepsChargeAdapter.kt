package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListAepsChargeBinding
import com.a2zsuvidhaa.`in`.model.KeyValue


class AepsChargeAdapter() : BaseRecyclerViewAdapter<KeyValue, ListAepsChargeBinding>(R.layout.list_aeps_charge) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListAepsChargeBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.data = item
    }
}