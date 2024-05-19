package com.example.budgetbuddy.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.models.Record
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class RecordsAdapter(precords: ArrayList<Record>) : RecyclerView.Adapter<RecordsAdapter.ViewHolder>(){

    val records : ArrayList<Record>
    lateinit var type : String
    lateinit var context : Context

    var onItemClick : ((Category) -> Unit)? = null
    var onItemLongClick : ((Record, View) -> Unit) = { record: Record, view: View -> false }

    init {
        this.records = precords
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.record_custom_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return this.records.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rec = this.records[position]


        if(type == "outgoings"){
            holder.txtAmount.setTextColor(Color.RED)
            holder.txtAmount.text = "-" + rec.amount.toString()
        }else{
            holder.txtAmount.setTextColor(Color.GREEN)
            holder.txtAmount.text = "+" + rec.amount.toString()
        }

        holder.txtDate.text = rec.date
        holder.txtAmount.text = rec.amount.toString()
        holder.txtConcept.text = rec.concept

        FirebaseRealtime().getCategoryFromId(Utils().getUserUID(context), rec.categoryId){
            holder.imgCat.setImageResource(it.icon)
            holder.imgCat.circleBackgroundColor = Color.parseColor("#" + it.bgcolor)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(rec, it)
            true
        }
    }

    fun filterByDay(){
        val calendar = Calendar.getInstance()
        val date = calendar.get(Calendar.DATE)

        val filteredList = records.filter { record ->
            record.getCalendar().get(Calendar.DATE) == date
        }
        updateData(filteredList)
    }

    fun filterByWeek(){
        val calendar = Calendar.getInstance()
        val Week = calendar.get(Calendar.WEEK_OF_YEAR)
        val Year = calendar.get(Calendar.YEAR)

        val filteredList = records.filter { record ->
            val c = record.getCalendar()
            c.get(Calendar.WEEK_OF_YEAR) == Week && c.get(Calendar.YEAR) == Year
        }
        updateData(filteredList)
    }

    fun filterByMonth(){
        val calendar = Calendar.getInstance()
        val Month = calendar.get(Calendar.MONTH)
        val Year = calendar.get(Calendar.YEAR)

        val filteredList = records.filter { record ->
            val c = record.getCalendar()
            c.get(Calendar.MONTH) == Month && c.get(Calendar.YEAR) == Year
        }
        updateData(filteredList)
    }

    fun filterByYear(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)

        val filteredList = records.filter { record ->
            record.getCalendar().get(Calendar.YEAR) == year
        }
        updateData(filteredList)
    }

    private fun updateData(filteredList: List<Record>) {
        records.clear()
        records.addAll(filteredList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var txtConcept : TextView
        var txtAmount : TextView
        var txtDate : TextView
        var imgCat : CircleImageView

        init {
            txtConcept = itemView.findViewById(R.id.txtConcept)
            txtAmount = itemView.findViewById(R.id.txtAmount)
            txtDate = itemView.findViewById(R.id.txtDate)
            imgCat = itemView.findViewById(R.id.imgCategoryRecord)
        }

    }
}