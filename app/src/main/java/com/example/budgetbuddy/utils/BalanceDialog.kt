package com.example.budgetbuddy.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.budgetbuddy.databinding.DialogBalanceBinding
import setupDecimalKeyListener


class BalanceDialog(
    private val onSubmitClickListener: (Double) -> Unit
) : DialogFragment(){

    private lateinit var binding : DialogBalanceBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBalanceBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)

        binding.etNewBalance.setupDecimalKeyListener()

        binding.btChangeBalance.setOnClickListener {
            onSubmitClickListener.invoke(binding.etNewBalance.text.toString().toDouble())
            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}