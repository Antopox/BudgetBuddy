package com.example.budgetbuddy.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.RecordsAdapter
import com.example.budgetbuddy.models.Record
import com.example.budgetbuddy.utils.BalanceDialog
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.Utils
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout

class BalanceFragment : Fragment(), OnChartValueSelectedListener,
    FirebaseRealtime.FirebaseRecordCallback {

    private lateinit var txtBalance: TextView
    private lateinit var btEditBalance: ImageButton
    private lateinit var currentUser: String
    private lateinit var donutChart: PieChart
    private lateinit var recView: RecyclerView
    private lateinit var tabType: TabLayout
    private lateinit var typeSelected: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = Utils().getUserUID(requireContext())
        Log.d("userUID", currentUser)

        donutChart = view.findViewById(R.id.chartBalance)
        txtBalance = view.findViewById(R.id.txtBalance)
        btEditBalance = view.findViewById(R.id.btEditBalance)
        recView = view.findViewById(R.id.recviewRecentRecords)
        tabType = view.findViewById(R.id.tabLayoutBalance)
        recView.layoutManager = LinearLayoutManager(context)

        btEditBalance.setOnClickListener {
            showBalanceDialog()
        }

        typeSelected = "incomes"

        userExist(currentUser)
        printDonutChartData(typeSelected)
        donutChart.setOnChartValueSelectedListener(this)

        tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                donutChart.clear()

                if (p0?.position == 0) {
                    typeSelected = "incomes"
                }else{
                    typeSelected = "outgoings"
                }
                printDonutChartData(typeSelected)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })
    }

    private fun showBalanceDialog() {
        BalanceDialog(
            onSubmitClickListener = {
                txtBalance.text = it.toString()
                FirebaseRealtime().changeBalance(currentUser, it)
            }
        ).show(parentFragmentManager, "BalanceDialog")
    }

    fun userExist(userUID: String) {

        FirebaseRealtime().getBalance(userUID){
            if (it == -1.0) {
                // No hay balance guardado
                showBalanceDialog()
            }else {
                txtBalance.text = it.toString()
            }
        }
    }

    private fun printDonutChartData(type: String) {
        val list: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()
        FirebaseRealtime().getDonutCategories(currentUser, type) { catsIds ->

            // Initialize a counter to track completed asynchronous operations
            var pendingTasks = catsIds.size

            for (catId in catsIds) {
                FirebaseRealtime().getCategoryFromId(currentUser, catId) { cat ->
                    colors.add(Color.parseColor("#" + cat.bgcolor))

                    FirebaseRealtime().getSumValueFromCat(currentUser, catId, type) { value ->
                        list.add(PieEntry(value, cat.name))
                        Log.d("pieEntry", "$value   ${cat.name}")

                        // Decrement the counter and check if all tasks are done
                        pendingTasks -= 1
                        if (pendingTasks == 0) {
                            // All tasks are done, update the chart
                            updateDonutChart(list, colors, type)
                        }
                    }
                }
            }
        }
    }

    private fun updateDonutChart(list: ArrayList<PieEntry>, colors: ArrayList<Int>, type: String) {
        val pieDataSet = PieDataSet(list, "")
        pieDataSet.setColors(colors)
        pieDataSet.valueTextSize = 15f
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueFormatter = DefaultValueFormatter(2)

        val pieData = PieData(pieDataSet)
        donutChart.data = pieData
        donutChart.description.isEnabled = false
        donutChart.legend.isEnabled = false
        donutChart.centerText = type
        donutChart.animateY(1500)
        donutChart.invalidate()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is PieEntry) {
            val pieEntry = e
            FirebaseRealtime().getCategoryIdFromName(currentUser, pieEntry.label){
                Log.d("pie Entry Name Clicked", pieEntry.label)
                Log.d("Category Id", it)
                FirebaseRealtime().getMonthRecordsFromCat(currentUser, it, typeSelected, this@BalanceFragment)
            }
        }
    }

    override fun onNothingSelected() {
    }

    override fun onRecordsLoaded(records: ArrayList<Record>) {
        Log.d("recordSize", records.size.toString())
        val adapter = RecordsAdapter(records)
        recView.adapter = adapter
    }

}