package com.example.budgetbuddy.models

import java.text.SimpleDateFormat
import java.util.Calendar

class Record() {
    var id : String = ""
    var categoryId : String = ""
    var date : String = ""
    var concept : String = ""
    var amount : Double = 0.00

    constructor(id: String, categoryId: String, date: String, concept: String, amount: Double): this(){
        this.id = id
        this.categoryId = categoryId
        this.date = date
        this.concept = concept
        this.amount = amount
    }

    fun getCalendar(): Calendar {
        val c = Calendar.getInstance()
        val d = SimpleDateFormat("dd/MM/yyyy").parse(this.date)
        c.time = d

        return c
    }
}