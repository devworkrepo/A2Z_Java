package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListGridUserBinding


class GridViewUserAdapter() : BaseRecyclerViewAdapter<String, ListGridUserBinding>(R.layout.list_grid_user) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListGridUserBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.tvTitle.text = item

        binding.cvContent.setOnClickListener{
            onItemClick?.invoke(it,item,position)
        }
    }
}