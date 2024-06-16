package com.example.budgetbuddy.models

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Modelo de registro (Gato/Ingreso)
 * @property id Identificador del registro en FirebaseRealtimeDatabase
 * @property categoryId Identificador de la categoría del registro
 * @property date Fecha del registro
 * @property concept Descripción del registro
 * @property amount cantidad del registro
 */
class Record {
    var id : String = ""
    var categoryId : String = ""
    var date : String = ""
    var concept : String = ""
    var amount : Double = 0.00

    /**
     * Devuelve un calendario con la fecha del registro
     */
    fun getCalendar(): Calendar {
        val c = Calendar.getInstance()
        val d = SimpleDateFormat("dd/MM/yyyy").parse(this.date)
        c.time = d

        return c
    }
}