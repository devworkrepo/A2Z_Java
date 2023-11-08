package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListAepsChargeBinding
import com.a2zsuvidhaa.`in`.databinding.ListTestItemBinding
import com.a2zsuvidhaa.`in`.model.KeyValue


class TestAdapter() : BaseRecyclerViewAdapter<Int, ListTestItemBinding>(R.layout.list_test_item) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListTestItemBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.tvTest.text = item.toString()
    }
}