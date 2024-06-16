package com.example.budgetbuddy.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.CategoriesAdapter
import com.example.budgetbuddy.databinding.DialogNewRecordBinding
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.models.Record
import com.google.android.material.tabs.TabLayout
import java.util.Calendar

/**
 * Diálogo para agregar un nuevo registro
 */
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

        binding.etNewRecordAmount.setupDecimalKeyListener()
        type = "incomes"
        catID = ""
        FirebaseRealtime().getCategories(Utils().getUserUID(requireContext()), this)
        binding.recViewNewRecCat.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)



        binding.tabOperationType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                type = if (p0?.position == 0){
                    "incomes"
                }else{
                    "outgoings"
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })

        binding.btAddNewRecord.setOnClickListener {

            if (checkValues()){
                val record = Record()
                val currentDate = Calendar.getInstance()
                record.date = "${currentDate.get(Calendar.DAY_OF_MONTH)}/${currentDate.get(Calendar.MONTH) + 1}/${currentDate.get(
                    Calendar.YEAR)}"

                record.amount = binding.etNewRecordAmount.text.toString().toDouble()
                record.concept = binding.etNewRecordConcept.text.toString()
                record.categoryId = catID

                onSubmitClickListener.invoke(record, type)
                dismiss()
            }
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCategoriesLoaded(cats: ArrayList<Category>) {
        if (cats.size == 0){
            Utils().toast(requireContext(), getString(R.string.no_categories))
            dismiss()
        }else{
            val adapter = CategoriesAdapter(cats)
            binding.recViewNewRecCat.adapter = adapter
            adapter.onItemClick = {
                catID = it.id
            }
        }
    }

    private fun checkValues(): Boolean{
        if (binding.etNewRecordConcept.text.toString().isEmpty()){
            Utils().toast(requireContext(), getString(R.string.enter_concept))
            return false

        }else if (binding.etNewRecordAmount.text.toString().isEmpty()){
            Utils().toast(requireContext(), getString(R.string.enter_amount))
            return false
        }else if (catID == ""){
            Utils().toast(requireContext(), getString(R.string.select_cat))
            return false
        }else{
            return true
        }
    }
}