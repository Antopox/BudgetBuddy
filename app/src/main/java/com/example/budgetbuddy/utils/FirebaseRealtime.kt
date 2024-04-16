package com.example.budgetbuddy.utils

import com.google.firebase.database.FirebaseDatabase

class FirebaseRealtime {
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference()

    fun createUserSchema(userUID: String, balance: Float){
        val reference = firebaseDatabase.child(userUID)

        reference.child("balance").setValue(balance)
    }
}