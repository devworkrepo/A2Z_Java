package com.a2zsuvidhaa.`in`.adapter.dmt

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BaseRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.DmtCommission
import com.a2zsuvidhaa.`in`.databinding.ListCalcChargeTransactionBinding
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.enums.DmtType


class DmtCommissionAdapter(
        private val dmtType: DmtType
) : BaseRecyclerViewAdapter<DmtCommission,
        ListCalcChargeTransactionBinding>(R.layout.list_calc_charge_transaction) {

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListCalcChargeTransactionBinding>, position: Int
    ) {

        if(dmtType == DmtType.WALLET_ONE){
            holder.binding.tvSerialNumber.hide()
        }

        holder.binding.item = items[position]

    }

}