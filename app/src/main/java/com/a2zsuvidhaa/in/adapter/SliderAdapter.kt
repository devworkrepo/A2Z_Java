package com.a2zsuvidhaa.`in`.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ListSliderBinding
import com.a2zsuvidhaa.`in`.model.Banner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class SliderAdapter : BaseRecyclerViewAdapter<Banner, ListSliderBinding>(R.layout.list_slider) {

    override fun onBindViewHolder(
            holder: Companion.BaseViewHolder<ListSliderBinding>, position: Int
    ) {
        val binding = holder.binding
        val bannerCardData = items[position]
        setupGlideImage(bannerCardData, binding)
    }

    private fun setupGlideImage(bannerCardData: Banner, binding: ListSliderBinding) {

        Glide.with(context!!)
                .load(bannerCardData.image)

                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.d("TAG_Glide", "onLoadFailed: ${e?.message}")
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                })
                .into(binding.imgBanner)
    }
}