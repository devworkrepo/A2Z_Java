package com.a2zsuvidhaa.`in`.adapter.user

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BasePagingRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.response.RegisterCompleteUser
import com.a2zsuvidhaa.`in`.databinding.ListCompleteUserBinding
import com.a2zsuvidhaa.`in`.util.ents.setupBGColor
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor

class CompleteUserPagingAdapter : BasePagingRecyclerViewAdapter<RegisterCompleteUser,
        ListCompleteUserBinding>(
    R.layout.list_complete_user, DiffUtilCallBack()
) {

    class DiffUtilCallBack : DiffUtil.ItemCallback<RegisterCompleteUser>() {
        override fun areItemsTheSame(
            oldItem: RegisterCompleteUser,
            newItem: RegisterCompleteUser
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RegisterCompleteUser,
            newItem: RegisterCompleteUser
        ): Boolean {
            return false
        }

    }

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListCompleteUserBinding>,
        position: Int
    ) {

        val item = this.getItem(position)
        val binding = holder.binding
        binding.item = item

        setupStatus(binding, item!!)
        binding.llContent.setOnClickListener{
            item.kycStatus = getKycStatus(item)
            onItemClick?.invoke(it,item,position)
        }


    }

    private fun getKycStatus(item: RegisterCompleteUser): Int {

        return if (item.panVerified == "YES"
            && item.aadhaarKyc == "YES"
            && item.documentKyc == "YES"
            && item.aepsKyc == "YES"
        ) 1
        else if (item.panVerified == "PENDING"
            && item.aadhaarKyc == "PENDING"
            && item.documentKyc == "PENDING"
            && item.aepsKyc == "PENDING"
        ) 3
        else 2
    }


    private fun setupStatus(binding: ListCompleteUserBinding, item: RegisterCompleteUser) {


        val (color: Int, imageIcon: Int, text: String) = when (getKycStatus(item)) {
            1 -> Triple(R.color.green, R.drawable.ic_check_tick, "Completed")
            2 -> Triple(R.color.grey_600, R.drawable.ic_baseline_info_24, "InComplete")
            else -> Triple(R.color.colorPrimaryDark, R.drawable.ic_baseline_info_24, "Pending")
        }


        binding.tvStatus.setupTextColor(color)
        binding.tvStatus.text = text
        binding.ivStatus.setColorFilter(ContextCompat.getColor(context!!, color))
        binding.ivStatus.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                imageIcon
            )
        )
    }
}
