package com.a2zsuvidhaa.`in`.adapter.dmt

import android.view.View
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BaseRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.SenderAccountDetail
import com.a2zsuvidhaa.`in`.databinding.ListRemitterBankInfoBinding
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.enums.DmtType


class AccountListAdapter(private val dmtType: DmtType) : BaseRecyclerViewAdapter<SenderAccountDetail, ListRemitterBankInfoBinding>(R.layout.list_remitter_bank_info) {

    override fun onBindViewHolder(
            holder: Companion.BaseViewHolder<ListRemitterBankInfoBinding>, position: Int
    ) {
        holder.binding.item = items[position]
        holder.binding.llContent.setOnClickListener{
            onItemClick?.invoke(it,items[position],position)
        }



        if(items[position].lastSuccessTime.orEmpty().isEmpty()){
            holder.binding.tvLastSuccess.hide()
        }
        else holder.binding.tvLastSuccess.show()

        val limit =   when(dmtType){
            DmtType.WALLET_ONE-> items[position].remainingLimit
            DmtType.WALLET_TWO-> items[position].remainingLimit2
            DmtType.WALLET_THREE-> items[position].remainingLimit3
            else -> ""
        }

        holder.binding.tvAvailBalance.text = "Available Limit : $limit"

        if(dmtType == DmtType.DMT_THREE){
            holder.binding.tvAvailBalance.hide()
        }
    }



}