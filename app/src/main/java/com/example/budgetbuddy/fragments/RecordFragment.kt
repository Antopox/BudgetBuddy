package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type

/**
 * Fragmento que muestra los registros del usuario.
 * -Agregar un nuevo registro.
 * -Filtrar por tipo de registro (Gasto/Ingreso).
 * -Filtrar por fecha (Día/Semana/Mes/Año).
 */
class RecordFragment : Fragment(), FirebaseRealtime.FirebaseRecordCallback {

    private lateinit var recview : RecyclerView
    private lateinit var fabAddNewRecord : FloatingActionButton
    private lateinit var tabType : TabLayout
    private lateinit var tabDayMonth : TabLayout
    private lateinit var useruid : String
    private lateinit var adapter : RecordsAdapter
    private lateinit var opTypeSelected : String
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
        opTypeSelected = "incomes"

        FirebaseRealtime().getRecords(useruid, opTypeSelected, this)

        //Dependiendo del tipo de registro seleccionado se cambia la lista de registros
        tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0){
                    opTypeSelected = "incomes"
                    FirebaseRealtime().getRecords(useruid, opTypeSelected, this@RecordFragment)

                }else{
                    opTypeSelected = "outgoings"
                    FirebaseRealtime().getRecords(useruid, opTypeSelected, this@RecordFragment)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        //Botón para agregar un nuevo registro
        fabAddNewRecord.setOnClickListener {
            NewRecordDialog(
                onSubmitClickListener = { record, type ->
                    FirebaseRealtime().addNewRecord(
                        Utils().getUserUID(requireContext()),
                        record,
                        type
                    )
                }
            ).show(parentFragmentManager, "NewRecordDialog")
        }

        //Dependiendo de la seleccion se filtra la lista de registros segun el dia, semana, mes o año
        tabDayMonth.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                FirebaseRealtime().getRecords(useruid, opTypeSelected, this@RecordFragment)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })
    }

    /**
     * Al cargarse los registros se muestra en el RecyclerView, se le añade un menu flotante y
     * se aplica el filtro seleccionado.
     */
    override fun onRecordsLoaded(records: ArrayList<Record>) {

        adapter = RecordsAdapter(records)

        adapter.onItemLongClick = { record: Record, view: View ->

            val popup = PopupMenu(requireContext(), view)
            popup.inflate(R.menu.menu_context_record)
            popup.setForceShowIcon(true)

            //Se muestra un menu flotante con la opcion de borrado
            popup.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.action_delete -> {
                        //Se abre el dialogo de confirmación de borrado
                        BeautifulDialog.build(requireActivity())
                            .title(getString(R.string.delete_record), titleColor = R.color.black)
                            .description(getString(R.string.are_you_sure), color = R.color.black)
                            .type(type = BeautifulDialog.TYPE.ALERT)
                            .position(BeautifulDialog.POSITIONS.CENTER)
                            .onPositive(text = getString(R.string.yes), shouldIDismissOnClick = true) {
                                Utils().toast(requireContext(), "Eliminado.")
                                records.remove(record)
                                FirebaseRealtime().deleteRecord(record, Utils().getUserUID(requireContext()), opTypeSelected)
                                adapter.notifyDataSetChanged()
                            }
                            .onNegative(text = getString(android.R.string.cancel)) {}
                    }
                }
                true
            }
            popup.show()
        }
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