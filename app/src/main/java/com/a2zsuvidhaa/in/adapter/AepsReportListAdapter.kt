package com.a2zsuvidhaa.`in`.adapter

import android.view.View
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListReportAepsBinding
import com.a2zsuvidhaa.`in`.model.AepsData
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor
import com.a2zsuvidhaa.`in`.util.ents.show


class AepsReportListAdapter() : BaseRecyclerViewAdapter<AepsData,
        ListReportAepsBinding>(R.layout.list_report_aeps) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListReportAepsBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item
        binding.tvStatus.text = item.status
        when (item.status_id.toInt()) {
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
        binding.btnComplain.setOnClickListener { onComplain?.invoke(item!!) }

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

        if (item.is_complain)
            binding.btnComplain.show()
        else binding.btnComplain.hide()


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

    private fun viewHideShow(item: AepsData, binding: ListReportAepsBinding) {
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

    var onPrint: ((item: AepsData) -> Unit)? = null
    var onComplain: ((item: AepsData) -> Unit)? = null
    var onCheckStatus: (( item: AepsData) -> Unit)? = null


}