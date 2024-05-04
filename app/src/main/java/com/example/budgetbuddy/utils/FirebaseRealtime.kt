package com.example.budgetbuddy.utils

import android.util.Log
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.models.Record
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRealtime {
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference()

    fun changeBalance(userUID: String, balance: Double){

        val reference = firebaseDatabase.child(userUID).child("balance")
        reference.setValue(balance)
    }

    fun getBalance(userUID: String): Double {
        val reference = firebaseDatabase.child(userUID).child("balance")
        var balance : Double = -1.0
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    balance = dataSnapshot.value.toString().toDouble()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                balance = -1.0
            }
        })

        return balance
    }

    fun addOrSubtractBalance(userUID: String, amount: Double, type: String){
        var balance = getBalance(userUID)

        if (balance != -1.0){
            if (type == "incomes"){
                balance += amount
            } else {
                balance -= amount
            }
            changeBalance(userUID, balance)
        }
    }

    interface FirebaseRecordCallback {
        fun onRecordsLoaded(records: ArrayList<Record>)
    }

    fun getImcomes(userUID: String, callback: FirebaseRecordCallback){

        val reference = firebaseDatabase.child(userUID).child("incomes")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var records = ArrayList<Record>()
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)
                    if (record != null) {
                        record.id = result.key.toString()
                        records.add(record)
                    }
                }
                callback.onRecordsLoaded(records)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    fun getOutgoings(userUID: String, callback: FirebaseRecordCallback){

        val reference = firebaseDatabase.child(userUID).child("outgoings")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var records = ArrayList<Record>()
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)
                    if (record != null) {
                        record.id = result.key.toString()
                        records.add(record)
                    }
                }
                callback.onRecordsLoaded(records)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    interface FirebaseCategoriesCallback{
        fun onCategoriesLoaded(cats: ArrayList<Category>)
    }

    fun getCategories(userUID: String, callback: FirebaseCategoriesCallback){

        val reference = firebaseDatabase.child(userUID).child("categories")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var categories = ArrayList<Category>()
                for (result in snapshot.children) {
                    val cat = result.getValue(Category::class.java)
                    if (cat != null) {
                        cat.id = result.key.toString()
                        categories.add(cat)
                    }
                }
                callback.onCategoriesLoaded(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    fun addNewRecord(userUID: String, rec: Record, type: String) {
        val newRecordRef = firebaseDatabase.child(userUID).child(type).push()

        val recordData = HashMap<String, Any>()
        recordData["date"] = rec.date
        recordData["categoryId"] = rec.categoryId
        recordData["concept"] = rec.concept
        recordData["amount"] = rec.amount

        addOrSubtractBalance(userUID, rec.amount, type)

        newRecordRef.setValue(recordData).addOnSuccessListener {
            Log.d("New Record", newRecordRef.key + "   " + rec.concept)
        }
    }
}