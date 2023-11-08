package com.a2zsuvidhaa.`in`.adapter.user

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.BasePagingRecyclerViewAdapter
import com.a2zsuvidhaa.`in`.data.response.RegisterInCompleteUser
import com.a2zsuvidhaa.`in`.databinding.ListInCompleteUserBinding
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor

class InCompleteUserPagingAdapter : BasePagingRecyclerViewAdapter<RegisterInCompleteUser,
        ListInCompleteUserBinding>(
    R.layout.list_in_complete_user, DiffUtilCallBack()
) {

    class DiffUtilCallBack : DiffUtil.ItemCallback<RegisterInCompleteUser>() {
        override fun areItemsTheSame(
            oldItem: RegisterInCompleteUser,
            newItem: RegisterInCompleteUser
        ): Boolean {
            return oldItem.mobile == newItem.mobile
        }

        override fun areContentsTheSame(
            oldItem: RegisterInCompleteUser,
            newItem: RegisterInCompleteUser
        ): Boolean {
            return false
        }

    }

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListInCompleteUserBinding>,
        position: Int
    ) {

        val item = this.getItem(position)!!
        val binding = holder.binding
        binding.item = item


        binding.llContent.setOnClickListener {
            onItemClick?.invoke(it, item, position)
        }

        setupTextImageView(
            binding.ivMobileVerified,
            binding.tvMobileVerified,
            item.isMobileVerified
        )

        setupTextImageView(
            binding.ivEmailVerified,
            binding.tvEmailVerified,
            item.isEmailVerified
        )
        setupTextImageView(
            binding.ivPanVerified,
            binding.tvPanVerified,
            item.isPanVerified
        )


    }

    private fun getItemStatus(item: String) = when (item) {
        "YES" -> 1
        "NO" -> 2
        "PENDING" -> 3
        else -> 0
    }

    private fun setupTextImageView(
        imageView: ImageView,
        textView: TextView,
        panVerified: String
    ) {
        val (color: Int, imageIcon: Int, data: String) = when (getItemStatus(panVerified)) {
            1 -> Triple(R.color.green, R.drawable.ic_check_tick, "Verified")
            2 -> Triple(R.color.red, R.drawable.ic_baseline_info_24, "Not-Verified")
            else -> Triple(R.color.colorPrimaryDark, R.drawable.ic_baseline_info_24, "Pending")
        }

        textView.apply {
            text = data
            setupTextColor(color)
        }

        imageView.apply {
            setImageDrawable(ContextCompat.getDrawable(context!!, imageIcon))
            setColorFilter(ContextCompat.getColor(context!!, color))
        }

    }


}
