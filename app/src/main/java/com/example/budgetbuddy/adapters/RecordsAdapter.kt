package com.example.budgetbuddy.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.Record
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

/**
 * Adaptador para los registros con métodos de filtrado por fecha
 * @param precords lista de registros
 */
class RecordsAdapter(precords: ArrayList<Record>) : RecyclerView.Adapter<RecordsAdapter.ViewHolder>(){

    private val records : ArrayList<Record> = precords
    lateinit var context : Context

    lateinit var onItemLongClick : ((Record, View) -> Unit)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.record_custom_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return this.records.size
    }

    /**
     * Setea los datos del registro y se asigna un long click listener
     * usando el atributo onItemLongClick(método que se añade en la activity)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rec = this.records[position]

        //Dependiendo del valor del amount se asigna un color
        if(rec.amount < 0){
            holder.txtAmount.setTextColor(Color.RED)
            holder.txtAmount.text = rec.amount.toString()
        }else{
            holder.txtAmount.setTextColor(Color.GREEN)
            holder.txtAmount.text = rec.amount.toString()
        }

        holder.txtDate.text = rec.date
        holder.txtAmount.text = rec.amount.toString()
        holder.txtConcept.text = rec.concept

        //Se busca la categoria a la que pertenece el registro para mostrar su icono
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
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        val filteredList = records.filter { record ->
            val c = record.getCalendar()
            c.get(Calendar.WEEK_OF_YEAR) == week && c.get(Calendar.YEAR) == year
        }
        updateData(filteredList)
    }

    fun filterByMonth(){
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val filteredList = records.filter { record ->
            val c = record.getCalendar()
            c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year
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

        var txtConcept : TextView = itemView.findViewById(R.id.txtConcept)
        var txtAmount : TextView = itemView.findViewById(R.id.txtAmount)
        var txtDate : TextView = itemView.findViewById(R.id.txtDate)
        var imgCat : CircleImageView = itemView.findViewById(R.id.imgCategoryRecord)
    }
}