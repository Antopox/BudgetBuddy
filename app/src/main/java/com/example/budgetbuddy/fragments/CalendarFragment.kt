package com.example.budgetbuddy.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.CalendarDayAdapter
import com.example.budgetbuddy.adapters.RecordsAdapter
import com.example.budgetbuddy.models.Record
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.Utils
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarView : CalendarView
    private lateinit var recViewRecordsOfDay : RecyclerView
    private lateinit var cardView : CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardView = view.findViewById(R.id.cardViewCalendar)
        calendarView = view.findViewById(R.id.calendarView)
        recViewRecordsOfDay = view.findViewById(R.id.recViewRecordsOfDay)
        recViewRecordsOfDay.layoutManager = LinearLayoutManager(requireContext())

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val date = "${data.date.dayOfMonth}/${data.date.monthValue}/${data.date.year}"
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    if (cardView.cardBackgroundColor.defaultColor == Color.WHITE) {
                        container.textView.setTextColor(Color.BLACK)
                    }else{
                        container.textView.setTextColor(Color.WHITE)
                    }

                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                container.recView.layoutManager = LinearLayoutManager(context)
                FirebaseRealtime().getRecordsForDate(Utils().getUserUID(requireContext()), date) {
                    container.records = it
                    Log.d("fecha y numReg", date + "   " + it.size)
                    container.recView.adapter = CalendarDayAdapter(it)
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                container.txtMonth.text = data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
                container.txtMonth.textSize = 18f

                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek : DayOfWeek

                            if (index + 1 >= daysOfWeek().size){
                                dayOfWeek = daysOfWeek()[0]
                            }else{
                                dayOfWeek = daysOfWeek()[index + 1]
                            }

                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)
        val endMonth = currentMonth.plusMonths(50)
        val daysOfWeek = daysOfWeek()
        calendarView.setup(startMonth, endMonth, daysOfWeek.first().plus(1))
        calendarView.scrollToMonth(currentMonth)

    }
    fun updateDayRecycler(records : ArrayList<Record>){
        recViewRecordsOfDay.adapter = RecordsAdapter(records)
    }
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.txtDayOfMonth)
        val recView: RecyclerView = view.findViewById(R.id.recviewCalendarDay)
        var records: ArrayList<Record> = ArrayList()
        init {
            view.setOnClickListener {
                updateDayRecycler(records)
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val txtMonth: TextView = view.findViewById(R.id.txtMonthCalendar)
        val titlesContainer: ViewGroup = view.findViewById(R.id.titlesContainer)
    }
}



