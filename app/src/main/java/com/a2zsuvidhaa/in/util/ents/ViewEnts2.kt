package com.a2zsuvidhaa.`in`.util.ents


import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.enums.LottieType
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso


inline fun EditText.afterTextChange(crossinline callback: (String) -> Unit) {
    val editText = this
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            val mText = editText.text.toString()
            callback(mText)
        }


    })
}


inline fun EditText.onTextChange(crossinline onChange: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChange(this@onTextChange.text.toString())
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
}

fun View.hide() {
    this.visibility = View.GONE
}


fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun EditText?.makeFocus() {
    this?.requestFocus()
    val imm: InputMethodManager? = this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


fun TextView.setupTextColor(color: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, color))
}

fun View.setupBGColor(color: Int) {
    this.setBackgroundColor(ContextCompat.getColor(this.context, color))
}


inline fun ImageView.setGlideImage(imgUrl: String, crossinline onComplete: () -> Unit = {}) {

    val imageView: ImageView = this


    Glide.with(this.context)
            .load(imgUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageDrawable(
                            ContextCompat.getDrawable(
                                    imageView.context,
                                    R.drawable.no_photo
                            )
                    )
                    onComplete()
                    return false
                }

                override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageDrawable(resource)
                    onComplete()
                    return false
                }

            }).submit()
}


fun ImageView.setupPicassoImage(source: String, onComplete: () -> Unit = {}) {

    AppUtil.logger("url : $source")
    try {

        Glide.with(this.context)
            .load(source)
            .addListener(object  :RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete()
                    return false
                }
            })
            .into(this)


   /*    Picasso.get()
                .load(source)
                .resize(659, 800)
                .centerCrop()
                .into(this, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        onComplete()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        onComplete()
                    }

                })*/
    } catch (e: Exception) {
    }
}

fun Button.setupVisibility(boolean: Boolean = true) {
    this.apply {
        if (boolean) {
            alpha = 1f
            isEnabled = true
            isClickable = true
        } else {
            alpha = 0.2f
            isEnabled = false
            isClickable = false
        }
    }

}

fun TabLayout.onTabSelectedListener(onTabSelected: (TabLayout.Tab) -> Unit) {
    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                onTabSelected(it)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}


fun RecyclerView.setup(fixSize: Boolean = false): RecyclerView {
    setHasFixedSize(fixSize)
    layoutManager = LinearLayoutManager(this.context)
    return this
}


fun LottieAnimationView.set(type: LottieType) {
    val fileName = when (type) {
        LottieType.ALERT -> "lottie_alert.json"
        LottieType.SUCCESS -> "lottie_success.json"
        LottieType.FAILURE -> "lottie_failed.json"
        LottieType.PENDING -> "lottie_pending.json"
        LottieType.WARNING -> "lottie_warning.json"
        LottieType.TIME_OUT -> "lottie_time_out.json"
        LottieType.NO_INTERNET -> "lottie_no_internet.json"
        LottieType.SERVER -> "lottie_server.json"
    }

    this.setAnimation(fileName)
    this.playAnimation()
}


fun Spinner.setupAdapter(list: Array<String>,
                         onItemSelected: (String) -> Unit
) {
    val dataAdapter = ArrayAdapter(context,
            R.layout.spinner_layout, list)
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = dataAdapter
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val value = this@setupAdapter.selectedItem.toString()
            onItemSelected(value)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}


fun AutoCompleteTextView.setup(list: ArrayList<String>, onSelected: (String) -> Unit) {
    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this.context,
            android.R.layout.simple_spinner_dropdown_item,
            list
    )

    setAdapter(adapter)
    onItemClickListener = AdapterView.OnItemClickListener { parent, arg1, position, id ->
        onSelected(list[position])
    }
}

fun Button.disable(value: Boolean = true) {

    if (value) {
        this.isEnabled = false
        this.isClickable = false
        this.alpha = 0.5f
    } else {
        this.isEnabled = true
        this.isClickable = true
        this.alpha = 1f
    }

}

fun Snackbar.withColor(colorInt: Int): Snackbar {

    val color = this.context.resources.getColor(colorInt)

    this.view.setBackgroundColor(color)
    return this
}

fun View.showSnackbar(message: String, backgroundColor: Int = R.color.black) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
            .withColor(R.color.green)
            .show()
}


fun EditText.preventCopyPaste() {
    this.customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {

        }

    }
}

fun View.setupBackground(@DrawableRes drawable: Int) {
    this.background = ContextCompat.getDrawable(this.context, drawable)
    setPadding(32, 16, 32, 16)
}

fun View.setupBackgroundColor(@DrawableRes drawable: Int) {
    this.background = ContextCompat.getDrawable(this.context, drawable)
    setPadding(32, 16, 32, 16)
}

fun CardView.setupMargin(left: Int = 8, right: Int = 8, top: Int = 8, bottom: Int = 8) {
    val cardViewMarginParams = this.layoutParams as ViewGroup.MarginLayoutParams
    cardViewMarginParams.setMargins(left.dp, top.dp, right.dp, bottom.dp)
    this.requestLayout()
}