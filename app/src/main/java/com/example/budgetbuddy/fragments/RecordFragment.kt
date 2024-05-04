package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.RecordsAdapter
import com.example.budgetbuddy.models.Record
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.NewRecordDialog
import com.example.budgetbuddy.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class RecordFragment : Fragment(), FirebaseRealtime.FirebaseRecordCallback {

    private lateinit var recview : RecyclerView
    private lateinit var fabAddNewRecord : FloatingActionButton
    private lateinit var tabType : TabLayout
    private lateinit var tabDayMonth : TabLayout
    private lateinit var useruid : String
    private lateinit var adapter : RecordsAdapter
    private var opTypeSelected = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recview = view.findViewById(R.id.recyclerRecords)
        tabType = view.findViewById(R.id.tabRecordType)
        fabAddNewRecord = view.findViewById(R.id.floatingBtAddRecord)
        tabDayMonth = view.findViewById(R.id.tabRecordDayMonth)
        useruid = Utils().getUserUID(requireContext())
        recview.layoutManager = LinearLayoutManager(context)
        FirebaseRealtime().getImcomes(useruid, this)

        tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0){
                    FirebaseRealtime().getImcomes(useruid, this@RecordFragment)
                    opTypeSelected = 0
                }else{
                    FirebaseRealtime().getOutgoings(useruid, this@RecordFragment)
                    opTypeSelected = 1
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        fabAddNewRecord.setOnClickListener(View.OnClickListener {
            NewRecordDialog (
                onSubmitClickListener = { record, type ->
                    FirebaseRealtime().addNewRecord(Utils().getUserUID(requireContext()), record, type)
                }
            ).show(parentFragmentManager, "NewRecordDialog")
        })

        tabDayMonth.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (opTypeSelected == 0){
                    FirebaseRealtime().getImcomes(useruid, this@RecordFragment)
                }else{
                    FirebaseRealtime().getOutgoings(useruid, this@RecordFragment)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })
    }

    override fun onRecordsLoaded(records: ArrayList<Record>) {
        adapter = RecordsAdapter(records)
        recview.adapter = adapter
        applySelectedFilter()
    }

    private fun applySelectedFilter() {
        when (tabDayMonth.selectedTabPosition) {
            0 -> adapter.filterByYear()
            1 -> adapter.filterByMonth()
            2 -> adapter.filterByWeek()
            3 -> adapter.filterByDay()
        }
        adapter.notifyDataSetChanged()
    }
}