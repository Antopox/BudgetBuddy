package com.example.budgetbuddy.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.IconAdapter
import com.example.budgetbuddy.databinding.DialogNewCategoryBinding
import com.example.budgetbuddy.models.Category
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener


class NewCategoryDialog(
    private val onSubmitClickListener: (Category) -> Unit) : DialogFragment() {

    private lateinit var binding : DialogNewCategoryBinding
    private var catColor : Int = 0
    private var iconID : Int = 0

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewCategoryBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)
        val adapter = IconAdapter(getIcons())
        adapter.onItemClick = {
            iconID = it
        }

        binding.recViewIcons.adapter = adapter
        binding.recViewIcons.layoutManager = GridLayoutManager(context, 4)

        binding.CatcolorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                // Do whatever you want with the color
                binding.colorPickerIndicator.circleBackgroundColor = color
                catColor = color
            }
        })
        binding.btNewCat.setOnClickListener {
            if(checkValues()){
                var category = Category()
                category.name = binding.etNewCatName.text.toString()
                category.bgcolor = catColor.toHexString()
                category.icon = iconID
                onSubmitClickListener.invoke(category)
                dismiss()
            }
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun getIcons(): List<Int>{
        val icons = listOf(
            R.mipmap.icon_cat_1_foreground,
            R.mipmap.icon_cat_2_foreground,
            R.mipmap.icon_cat_3_foreground,
            R.mipmap.icon_cat_4_foreground,
            R.mipmap.icon_cat_5_foreground,
            R.mipmap.icon_cat_6_foreground,
            R.mipmap.icon_cat_7_foreground,
            R.mipmap.icon_cat_8_foreground,
            R.mipmap.icon_cat_9_foreground,
            R.mipmap.icon_cat_10_foreground,
            R.mipmap.icon_cat_11_foreground,
            R.mipmap.icon_cat_12_foreground
        )
        return icons

    }

    fun checkValues(): Boolean{
        if(binding.etNewCatName.text.toString().isEmpty()){
            Utils().toast(requireContext(), getString(R.string.enter_cat_name))
            return false
        }else if(catColor == 0){
            Utils().toast(requireContext(), getString(R.string.select_cat_color))
            return false
        }else if(iconID == 0){
            Utils().toast(requireContext(), getString(R.string.select_cat_icon))
            return false
        }else {
            return true
        }
    }
}
