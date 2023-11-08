package com.a2zsuvidhaa.`in`.adapter

import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.dmt.UpiBank
import com.a2zsuvidhaa.`in`.data.model.dmt.UpiExtension
import com.a2zsuvidhaa.`in`.databinding.ListUpiBankBinding
import com.a2zsuvidhaa.`in`.util.ents.setupBackground
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor
import com.a2zsuvidhaa.`in`.util.ents.showToast
import java.util.*


class VpaBankListAdapter() : BaseRecyclerViewAdapter<UpiBank, ListUpiBankBinding>(R.layout.list_upi_bank) {

    private lateinit var mHolder: Companion.BaseViewHolder<ListUpiBankBinding>
    var isClickable = true
    private lateinit var previousBinding: ListUpiBankBinding



    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListUpiBankBinding>, position: Int) {

        mHolder = holder
        val binding = holder.binding
        val item = items[position]
        binding.tvUpiName.text = item.name

        holder.bindingAdapterPosition

        binding.tvUpiName.setOnClickListener {
            if (isClickable) {

                when (item.name?.toLowerCase(Locale.ROOT)){
                    "google pay"-> binding.tvUpiName.setupBackground(R.drawable.bg_green_5)
                    "phone pay","phone pe"-> binding.tvUpiName.setupBackground(R.drawable.bg_purple_5)
                    "paytm"-> binding.tvUpiName.setupBackground(R.drawable.bg_light_blue_5)
                    "amazon"-> binding.tvUpiName.setupBackground(R.drawable.bg_warning_5)
                    else-> binding.tvUpiName.setupBackground(R.drawable.bg_light_blue_5)
                }
                binding.tvUpiName.setupTextColor(R.color.white)

                previousBinding.tvUpiName.setupBackground(R.drawable.bg_grey_5)
                previousBinding.tvUpiName.setupTextColor(R.color.white2)

                previousBinding = binding

                onItemClick?.invoke(it, item, position)
            } else {
                context?.showToast("Reset Verification details to change upi bank")
            }
        }

        if (item.name == "Bank Upi") {
            previousBinding = binding
            binding.tvUpiName.setupBackground(R.drawable.bg_light_blue_5)
            binding.tvUpiName.setupTextColor(R.color.white)
        } else {
            binding.tvUpiName.setupBackground(R.drawable.bg_grey_5)
            binding.tvUpiName.setupTextColor(R.color.white2)
        }
    }


}


class VpaBankExtensionListAdapter(private val name : String?) : BaseRecyclerViewAdapter<UpiExtension, ListUpiBankBinding>(R.layout.list_upi_bank) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListUpiBankBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.tvUpiName.text = item.name

        when (name?.toLowerCase(Locale.ROOT)){
            "google pay"-> binding.tvUpiName.setupBackground(R.drawable.bg_green_5)
            "phone pay","phone pe"-> binding.tvUpiName.setupBackground(R.drawable.bg_purple_5)
            "paytm"-> binding.tvUpiName.setupBackground(R.drawable.bg_light_blue_5)
            "amazon"-> binding.tvUpiName.setupBackground(R.drawable.bg_warning_5)
            else -> binding.tvUpiName.setupBackground(R.drawable.bg_grey_5)
        }

        binding.tvUpiName.setOnClickListener {
            onItemClick?.invoke(it, item, position)
        }
    }
}
