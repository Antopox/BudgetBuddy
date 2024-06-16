package com.example.budgetbuddy.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.Record

/**
 * Adaptador para el calendario de la pantalla calendar
 * @param precords lista de registros para mostrar al hacer click en un día
 */
class CalendarDayAdapter(precords: ArrayList<Record>) : RecyclerView.Adapter<CalendarDayAdapter.CalendarDayViewHolder>() {

    private val records: ArrayList<Record> = precords

    class CalendarDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgBarraCalendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarDayViewHolder(view)
    }

    /**
     * Método que establece los datos en la vista.
     * Dependiendo si es un gasto o un ingreso aparecera una barra de color rojo o verde
     * bajo el TextView del día.
     */
    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        val record = records[position]
        if (record.amount < 0) {
            holder.imageView.setBackgroundColor(Color.RED)
        }else{
            holder.imageView.setBackgroundColor(Color.GREEN)
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }
}