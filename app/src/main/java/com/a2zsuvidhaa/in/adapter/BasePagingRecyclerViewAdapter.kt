package com.a2zsuvidhaa.`in`.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingRecyclerViewAdapter<T : Any, VB : ViewDataBinding>(
    private val layout: Int,
    diffUtil: DiffUtil.ItemCallback<T>
) :
    PagingDataAdapter<T, BasePagingRecyclerViewAdapter.Companion.BaseViewHolder<VB>>(diffUtil) {


    var context: Context? = null

    var onItemClick: ((view: View, item: T, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseViewHolder<VB>(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
        )

    companion object {
        class BaseViewHolder<VB : ViewDataBinding>(val binding: VB) :
            RecyclerView.ViewHolder(binding.root)
    }

}