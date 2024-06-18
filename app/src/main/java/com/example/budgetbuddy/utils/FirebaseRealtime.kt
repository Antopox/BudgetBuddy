package com.example.budgetbuddy.utils

import android.util.Log
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.models.Record
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

/**
 * Librería con las funciones para interactuar con la base de datos de Firebase
 * @property firebaseDatabase referencia a la base de datos de Firebase
 */
class FirebaseRealtime {
    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference()

    fun changeBalance(userUID: String, balance: Double) {
        val reference = firebaseDatabase.child(userUID).child("balance")
        val formattedBalance = BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).toDouble()
        reference.setValue(formattedBalance)
    }

    fun getBalance(userUID: String, callback: (Double) -> Unit) {
        val reference = firebaseDatabase.child(userUID).child("balance")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val balance = if (dataSnapshot.exists()) {
                    dataSnapshot.getValue(Double::class.java) ?: -1.0
                } else {
                    -1.0
                }
                val formattedBalance = BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).toDouble()
                callback(formattedBalance)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(-1.0)
            }
        })
    }

    private fun addOrSubtractBalance(userUID: String, amount: Double, type: String) {
        getBalance(userUID) { balance ->
            if (balance != -1.0) {
                var newBalance = BigDecimal(balance)
                val adjustment = BigDecimal(amount)

                newBalance = if (type == "incomes") {
                    newBalance.add(adjustment)
                } else {
                    newBalance.subtract(adjustment)
                }

                val formattedBalance = newBalance.setScale(2, RoundingMode.HALF_UP).toDouble()
                changeBalance(userUID, formattedBalance)
            }
        }
    }

    interface FirebaseRecordCallback {
        fun onRecordsLoaded(records: ArrayList<Record>)
    }

    fun getRecords(userUID: String, type: String, callback: FirebaseRecordCallback){

        val reference = firebaseDatabase.child(userUID).child(type)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = ArrayList<Record>()
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)
                    if (record != null) {
                        if (type == "outgoings") {
                            record.amount = -record.amount
                        }
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
                val categories = ArrayList<Category>()
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

    fun deleteRecord(rec: Record, userUID: String, type: String) {

        val recordRef = firebaseDatabase.child(userUID).child(type).child(rec.id)
        recordRef.removeValue()
        addOrSubtractBalance(userUID, rec.amount, type)
    }

    fun getCategoryFromId(userUID: String, categoryId: String, callback: (Category) -> Unit) {

        val reference = firebaseDatabase.child(userUID).child("categories").child(categoryId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        callback(category)
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error if needed and return null
                Log.e("FirebaseError", "Error getting category: ${error.message}")
            }
        })
    }

    fun getCategoryIdFromName(userUID: String, categoryName: String, callback: (String) -> Unit) {

        val reference = firebaseDatabase.child(userUID).child("categories").orderByChild("name").equalTo(categoryName)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        callback(snapshot.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error if needed and return null
                Log.e("FirebaseError", "Error getting category: ${error.message}")
            }
        })
    }

    fun addCategory(userUID: String, it: Category) {
        val reference = firebaseDatabase.child(userUID).child("categories").push()

        val catData = HashMap<String, Any>()
        catData["name"] = it.name
        catData["bgcolor"] = it.bgcolor
        catData["icon"] = it.icon

        reference.setValue(catData).addOnSuccessListener {
            Log.d("New Category", reference.key + "   " + catData["name"])
        }
    }

    fun getSumValueFromCat(userUID: String, catId: String, type: String, callback: (Float) -> Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val reference = firebaseDatabase.child(userUID).child(type).orderByChild("categoryId").equalTo(catId)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var sum = 0.0
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)

                    if (record != null) {
                        val c = record.getCalendar()
                        Log.d("month", c.get(Calendar.MONTH).toString() + "/" + c.get(Calendar.YEAR).toString() + "  " + month.toString())
                        if (c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year){
                            sum += record.amount
                        }
                    }
                }

                callback(sum.toFloat())
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error aquí
            }
        })
    }

    fun getDonutCategories(userUID: String, type: String, callback: (MutableSet<String>) -> Unit) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val reference = firebaseDatabase.child(userUID).child(type).orderByChild("date").endAt("${month}/${year}")
        val uniqueCatIds: MutableSet<String> = mutableSetOf()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (result in snapshot.children) {
                    val catID = result.child("categoryId").getValue(String::class.java)

                    if (catID != null) {
                        uniqueCatIds.add(catID)
                        Log.d("catID", catID)
                    }
                }
                Log.d("uniqueCatIds", uniqueCatIds.size.toString())
                callback(uniqueCatIds)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error aquí
            }
        })
    }

    fun getMonthRecordsFromCat(userUID: String, catId: String, type: String, callback: FirebaseRecordCallback) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val reference = firebaseDatabase.child(userUID).child(type).orderByChild("categoryId").equalTo(catId)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = ArrayList<Record>()
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)
                    if (record != null) {
                        if (record.getCalendar().get(Calendar.MONTH) == month && record.getCalendar().get(Calendar.YEAR) == year) {
                            record.id = result.key.toString()
                            records.add(record)
                        }
                    }
                }
                callback.onRecordsLoaded(records)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    fun getRecordsForDate(userUID: String, date: String, callback: (ArrayList<Record>) -> Unit) {
        val incomesRef = firebaseDatabase.child(userUID).child("incomes").orderByChild("date").equalTo(date)
        val outgoingsRef = firebaseDatabase.child(userUID).child("outgoings").orderByChild("date").equalTo(date)

        val records = ArrayList<Record>()
        var inReady = false
        var outReady = false

        records.clear()

        fun checkAndNotify() {
            if (inReady || outReady) {
                callback(records)
            }
        }

        incomesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val record = snapshot.getValue(Record::class.java)
                    if (record != null) {
                        record.id = snapshot.key.toString()
                        records.add(record)
                    }
                }
                inReady = true
                checkAndNotify()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
        outgoingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val record = snapshot.getValue(Record::class.java)
                    if (record != null) {
                        record.id = snapshot.key.toString()
                        record.amount = -record.amount
                        records.add(record)
                    }
                }
                outReady = true
                checkAndNotify()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })


    }

    fun categoryHasRecords(userUID: String, catid: String, callback: (Boolean) -> Unit) {
        val incomesRef = firebaseDatabase.child(userUID).child("incomes").orderByChild("categoryId").equalTo(catid)
        val outgoingsRef = firebaseDatabase.child(userUID).child("outgoings").orderByChild("categoryId").equalTo(catid)

        var hasRecords = false

        incomesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (result in snapshot.children) {
                    val record = result.getValue(Record::class.java)
                    if (record != null) {
                        hasRecords = true
                        break
                    }
                }

                outgoingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (result in snapshot.children) {
                            val record = result.getValue(Record::class.java)
                            if (record != null) {
                                hasRecords = true
                                break
                            }
                        }
                        callback(hasRecords)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    fun deleteCategory(userUID: String, cat: Category) {

        val catRef = firebaseDatabase.child(userUID).child("categories").child(cat.id)
        catRef.removeValue()
    }

    fun deleteUser(userUID: String) {
        val userRef = firebaseDatabase.child(userUID)
        userRef.removeValue()
    }
}