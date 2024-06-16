package com.example.budgetbuddy.utils

import android.content.Context
import android.widget.Toast
import com.example.budgetbuddy.R
import com.google.firebase.auth.FirebaseUser

/**
 * Librería de funciones útiles y sobre las preferencias del usuario
 */
class Utils {

    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createUserPreferences(c: Context, user: FirebaseUser){
        val prefs =
            c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

        prefs.putString("name", user.displayName)
        prefs.putString("email", user.email)
        prefs.putString("UID", user.uid)
        prefs.putBoolean("isSignedIn", true)
        prefs.apply()
    }

    fun getUserUID(c: Context): String{

        val prefs =
            c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val userUID = prefs.getString("UID", "").toString()

        return userUID
    }

    fun deleteUserPreferences(c: Context){
        val prefs = c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
    }

    fun getIsSignedIn(c: Context): Boolean{
        val isSigned: Boolean

        val prefs =
            c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        isSigned = prefs.getBoolean("isSignedIn", false)

        return isSigned
    }
}