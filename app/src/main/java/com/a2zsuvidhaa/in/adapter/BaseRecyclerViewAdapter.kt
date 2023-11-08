package com.a2zsuvidhaa.`in`.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupMargin
import com.a2zsuvidhaa.`in`.util.ents.show

abstract class BaseRecyclerViewAdapter<T : Any, VB : ViewDataBinding>(
    private val layout: Int
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.Companion.BaseViewHolder<VB>>() {

    lateinit var recyclerView: RecyclerView

    var items = mutableListOf<T>()
    fun addItems(items: List<T>) {
        this.items.clear()
        this.items = items as MutableList<T>
        notifyDataSetChanged()
    }

    fun addItemsWithClear(items: List<T>) {
        this.items = items as MutableList<T>
        notifyDataSetChanged()
    }

    var context: Context? = null

    override fun getItemCount() = items.size

    var onItemClick: ((view: View, item: T, position: Int) -> Unit)? = null


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder<VB>(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
    )

    companion object {
        class BaseViewHolder<VB : ViewDataBinding>(val binding: VB) :
            RecyclerView.ViewHolder(binding.root)
    }

    fun expand(
            linearLayout: LinearLayout,
            cardView: CardView,
            isExpand: Boolean,

    ) {
        if (isExpand) {
            linearLayout.show()
            cardView.setupMargin(4, 4, 4, 4)
        } else {
            linearLayout.hide()
            cardView.setupMargin(8, 8, 4, 4)


        }
    }


}