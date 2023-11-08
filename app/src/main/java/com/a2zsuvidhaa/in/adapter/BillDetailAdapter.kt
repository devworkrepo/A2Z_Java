package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListBillBinding
import com.a2zsuvidhaa.`in`.model.KeyValue
import com.a2zsuvidhaa.`in`.util.ents.hide


class BillDetailAdapter() : BaseRecyclerViewAdapter<KeyValue, ListBillBinding>(R.layout.list_bill) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListBillBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

        if (item.key == "null" || item.value == "null") binding.llList.hide()
    }
}