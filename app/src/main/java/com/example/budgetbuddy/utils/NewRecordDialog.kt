package com.example.budgetbuddy.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.adapters.CategoriesAdapter
import com.example.budgetbuddy.databinding.DialogNewRecordBinding
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.models.Record
import com.google.android.material.tabs.TabLayout
import setupDecimalKeyListener
import java.util.Calendar

class NewRecordDialog(
    private val onSubmitClickListener: (record: Record, type: String) -> Unit
) : DialogFragment(), FirebaseRealtime.FirebaseCategoriesCallback{

    private lateinit var binding : DialogNewRecordBinding
    private lateinit var type : String
    private lateinit var catID : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewRecordBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)
        type = "incomes"
        catID = ""
        val calendar = Calendar.getInstance()
        FirebaseRealtime().getCategories(Utils().getUserUID(requireContext()), this)
        binding.recViewNewRecCat.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.etNewRecordAmount.setupDecimalKeyListener()

        binding.tabOperationType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0){
                    type = "incomes"
                }else{
                    type = "outgoings"
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })

        binding.btAddNewRecord.setOnClickListener {

            val record = Record()
            val currentDate = Calendar.getInstance()
            record.date = "${currentDate.get(Calendar.DAY_OF_MONTH)}/${currentDate.get(Calendar.MONTH) + 1}/${currentDate.get(Calendar.YEAR)}"

            record.amount = binding.etNewRecordAmount.text.toString().toDouble()
            record.concept = binding.etNewRecordConcept.text.toString()
            record.categoryId = catID

            onSubmitClickListener.invoke(record, type)
            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCategoriesLoaded(cats: ArrayList<Category>) {
        val adapter = CategoriesAdapter(cats)
        binding.recViewNewRecCat.adapter = adapter
        adapter.onItemClick = {
            catID = it.id
        }
    }
}