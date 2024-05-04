package com.example.budgetbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.BalanceDialog
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class BalanceFragment : Fragment() {

    private lateinit var txtBalance : TextView
    private lateinit var btEditBalance : ImageButton
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        txtBalance = view.findViewById(R.id.txtBalance)
        btEditBalance = view.findViewById(R.id.btEditBalance)

        btEditBalance.setOnClickListener{
            showBalanceDialog()
        }
        userExist(currentUser.uid)
    }

    private fun showBalanceDialog(){
        BalanceDialog(
            onSubmitClickListener = {
                txtBalance.text = it.toString()
                FirebaseRealtime().changeBalance(currentUser.uid, it)
            }
        ).show(parentFragmentManager, "BalanceDialog")
    }

    fun userExist(userUID: String){
        var balance = FirebaseRealtime().getBalance(userUID)

        if (balance != -1.0){
            txtBalance.text = balance.toString()
        } else {
            // No hay balance guardado
            showBalanceDialog()
        }


    }
}