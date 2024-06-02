package com.example.budgetbuddy.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.Record

class CalendarDayAdapter(precords: ArrayList<Record>) : RecyclerView.Adapter<CalendarDayAdapter.CalendarDayViewHolder>() {

    var records: ArrayList<Record>
    init {
        this.records = precords
    }
    class CalendarDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgBarraCalendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarDayViewHolder(view)
    }

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