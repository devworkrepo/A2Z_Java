package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.MiniStatement
import com.a2zsuvidhaa.`in`.databinding.ListMiniStatementBinding
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor


class MiniStatementAdapter() : BaseRecyclerViewAdapter<MiniStatement, ListMiniStatementBinding>(R.layout.list_mini_statement) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListMiniStatementBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.miniStatement = item

        if (item.txnType == "Dr")
            binding.tvAmount.setupTextColor(R.color.red)
        else binding.tvAmount.setupTextColor(R.color.success)
    }
}