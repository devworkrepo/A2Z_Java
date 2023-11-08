package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListReportMatmBinding
import com.a2zsuvidhaa.`in`.model.MatmReportData
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor
import com.a2zsuvidhaa.`in`.util.ents.show


class MatmReportAdapter() : BaseRecyclerViewAdapter<MatmReportData,
        ListReportMatmBinding>(R.layout.list_report_matm) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListReportMatmBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item
        when (item.status) {
            1 -> binding.tvStatus.setupTextColor(R.color.green)
            2 -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.colorPrimaryDark)
        }
        binding.llMainLayout.setOnClickListener {
            viewHideShow(item, binding)
        }

        binding.btnPrint.setOnClickListener {
            onPrint?.invoke(item)
        }

        binding.btnCheckStatus.setOnClickListener {
            onCheckStatus?.invoke(item)
        }

        binding.tvItemCount.text =( position+1).toString()
        binding.tvItemCount2.text =( position+1).toString()

        if(item.is_print){
            binding.btnPrint.show()
        }
        else binding.btnPrint.hide()

        if(item.is_check_status){
            binding.btnCheckStatus.show()
        }
        else binding.btnCheckStatus.hide()

        if (item.isHide) {
            binding.run {
                llArrowHideable.show()
                llHideable.hide()
                }
        } else {
            binding.run {
                llArrowHideable.hide()
                llHideable.show()
                 }
        }


    }

    private fun viewHideShow(item: MatmReportData, binding: ListReportMatmBinding) {
        if (item.isHide) {
            binding.run {
                llArrowHideable.hide()
                llHideable.show()
                item.isHide = false }
        } else {
            binding.run {
                llArrowHideable.show()
                llHideable.hide()
                item.isHide = true }
        }
    }

    var onPrint: ((item: MatmReportData) -> Unit)? = null
    var onCheckStatus: (( item: MatmReportData) -> Unit)? = null


}