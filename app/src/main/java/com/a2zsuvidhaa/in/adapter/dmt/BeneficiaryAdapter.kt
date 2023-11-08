package com.a2zsuvidhaa.`in`.adapter.dmt

import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BaseRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.Beneficiary
import com.a2zsuvidhaa.`in`.databinding.ListBeneficiaryBinding
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import java.util.*


class BeneficiaryAdapter(
        private val dmtType: DmtType,
        private val highLightFirst : Boolean = false
) :
    BaseRecyclerViewAdapter<Beneficiary, ListBeneficiaryBinding>(
            R.layout.list_beneficiary
    ) {

    private var expandedModel: Beneficiary? = null



    override fun onBindViewHolder(
            holder: Companion.BaseViewHolder<ListBeneficiaryBinding>,
            position: Int
    ) {

        val binding = holder.binding
        val model = items[position]
        binding.item = model

        if(dmtType == DmtType.UPI){
            binding.tvTitleAccountNumber.text = "Upi Id"
            binding.tvBankNameTitle.text = "Provider"
        }

        if(highLightFirst && position == 0){
          binding.cardView.setupBGColor(R.color.yellow_light);
        }
        else binding.cardView.setupBGColor(R.color.white)


        expand(binding.llTransferContent, binding.cardView, false)

        binding.cardView.setOnClickListener {
            onRootItemClick(binding, model)
        }




       binding.llPay.setOnClickListener {
            onTransactionButtonClick?.invoke(items[position])
        }


        binding.btnDelete.setOnClickListener {
            onDeleteBeneficiary?.invoke(items[position])
        }

        binding.btnVerify.setOnClickListener {
            onVerifyBeneficiary?.invoke(items[position])
        }



        setupIsVerify(model, binding)

        if(model.lastSuccessTime.orEmpty().isEmpty()){
            binding.tvLastSuccess.hide()
        }
        else binding.tvLastSuccess.show()

        setupDmtTypeField(binding)
    }

    private fun setupDmtTypeField(binding: ListBeneficiaryBinding) {
       if(dmtType == DmtType.UPI){
           binding.apply {
               llIfsc.hide()
           }
       }
    }

    private fun setupIsVerify(
            model: Beneficiary,
            binding: ListBeneficiaryBinding
    ) {
        if (model.bankVerified == 1 || model.upiBankVerified == 1) {

            binding.tvBeneficiaryName.setupTextColor(R.color.green)
            binding.btnVerify.text = "Re-Verify"
            binding.ivVerifiedCheck.show()

        } else {
            binding.tvBeneficiaryName.setupTextColor(R.color.black)
            binding.btnVerify.text = "Verify"
            binding.ivVerifiedCheck.hide()

        }
    }

    private fun onRootItemClick(binding: ListBeneficiaryBinding, model: Beneficiary) {

        when (expandedModel) {
            null -> {
                expand(binding.llTransferContent, binding.cardView, true)
                expandedModel = model

            }
            model -> {
                expand(binding.llTransferContent, binding.cardView, false)
                expandedModel = null

            }
            else -> {

                val expandedModelPosition = items.indexOf(expandedModel!!)
                val oldBinding =
                    recyclerView.findViewHolderForAdapterPosition(expandedModelPosition)
                            as? Companion.BaseViewHolder<ListBeneficiaryBinding>

                if (oldBinding != null){
                    expand(oldBinding.binding.llTransferContent, oldBinding.binding.cardView, false)
                }

                expand(binding.llTransferContent, binding.cardView, true)
                expandedModel = model

            }
        }

    }

    fun swapItems(itemAIndex: Int, itemBIndex: Int) {
        val itemA: Beneficiary = items[itemAIndex]
        val itemB: Beneficiary = items[itemBIndex]
        items[itemAIndex] = itemB
        items[itemBIndex] = itemA
        notifyItemRangeChanged(itemAIndex,itemBIndex) //This will trigger onBindViewHolder method from the adapter.
    }


    var onTransactionButtonClick: ((
            beneficiary: Beneficiary
    ) -> Unit)? = null


    var onDeleteBeneficiary: ((
            beneficiary: Beneficiary
    ) -> Unit)? = null

    var onVerifyBeneficiary: ((
            beneficiary: Beneficiary
    ) -> Unit)? = null
}