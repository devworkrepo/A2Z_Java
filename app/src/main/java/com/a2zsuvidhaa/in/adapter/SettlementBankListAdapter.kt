package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListAepsSettlementBinding
import com.a2zsuvidhaa.`in`.model.BankDetail
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor
import com.a2zsuvidhaa.`in`.util.ents.show

class SettlementBankListAdapter() :
        BaseRecyclerViewAdapter<BankDetail, ListAepsSettlementBinding>(R.layout.list_aeps_settlement) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListAepsSettlementBinding>, position: Int) {

        val bankDetail : BankDetail = items[position]
        holder.binding.item = bankDetail

        holder.binding.let {
            when (bankDetail.status) {
                "1" -> {
                    it.btnUploadDocument.hide()
                    it.tvDocUploaded.hide()
                    it.ivSend.show()
                    //it.tvMessage.setupTextColor(R.color.colorPrimary)
                    it.tvStatus.setupTextColor(R.color.green)
                    it.tvStatus.text = "Active"
                }
                "3" -> {
                    it.ivSend.hide()
                   // it.tvMessage.setupTextColor(R.color.warning)
                    it.tvStatus.setupTextColor(R.color.yellow_dark)
                    it.tvStatus.text = "Pending for\napproval"

                    if(bankDetail.document_status == 0)
                    {
                        it.btnUploadDocument.show()
                        it.tvDocUploaded.hide()
                    }
                    else {
                        it.btnUploadDocument.hide()
                        it.tvDocUploaded.show()
                    }

                }
                else -> {
                    it.btnUploadDocument.hide()
                    it.tvDocUploaded.hide()
                    it.ivSend.hide()
                   //it.tvMessage.setupTextColor(R.color.failed)
                    it.tvStatus.setupTextColor(R.color.red)
                    it.tvStatus.text = "Rejected"
                }
            }




            it.btnUploadDocument.setOnClickListener {
               onUploadDocs?.invoke(bankDetail.id)
            }


            it.ivSend.setOnClickListener{_->
                if(bankDetail.status == "1")
                    onItemClick?.invoke(it.ivSend,bankDetail,position)
            }
        }
    }

    var onUploadDocs : ((id : String)->Unit)? = null

}