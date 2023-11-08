package com.a2zsuvidhaa.`in`.adapter

import android.graphics.ColorFilter
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.response.RegistrationRole
import com.a2zsuvidhaa.`in`.databinding.ListRegistrationRoleBinding
import com.a2zsuvidhaa.`in`.util.ents.setupBGColor
import com.a2zsuvidhaa.`in`.util.ents.setupMargin
import com.a2zsuvidhaa.`in`.util.ents.setupTextColor


class RoleListAdapter() :
    BaseRecyclerViewAdapter<RegistrationRole, ListRegistrationRoleBinding>(R.layout.list_registration_role) {


    private var preSelected: RegistrationRole? = null
    private var preBinding : ListRegistrationRoleBinding? = null

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListRegistrationRoleBinding>,
        position: Int
    ) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

        binding.cvItem.setOnClickListener {
            if(preSelected ==null){
                preSelected = item
                preBinding = binding
                setupCardTheme(binding,true)
                onSelected?.invoke(item,true)
            }
            else{

                if(preSelected == item){
                    setupCardTheme(binding,false)
                    preBinding = null
                    preSelected = null
                    onSelected?.invoke(item,false)
                }
                else{
                    setupCardTheme(preBinding!!,false)
                    setupCardTheme(binding,true)
                    preBinding = binding
                    preSelected = item
                    onSelected?.invoke(item,true)

                }
            }
        }
    }

    private fun setupCardTheme(binding: ListRegistrationRoleBinding,isSelected : Boolean = false) {


        val color = if(isSelected) R.color.white else R.color.black
        val cardColor = if(isSelected) R.color.green else R.color.white

        binding.cvItem.apply {
            setCardBackgroundColor(ContextCompat.getColor(context,cardColor))
            radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                context.resources.displayMetrics
            )

        }


        binding.tvTitle.setupTextColor(color)
        binding.ivCheck.apply {
            setColorFilter(ContextCompat.getColor(context,color))
        }

    }


     var onSelected  : ((role : RegistrationRole,visible : Boolean)->Unit)? = null
    }