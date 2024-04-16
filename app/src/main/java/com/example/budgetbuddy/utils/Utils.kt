package com.example.budgetbuddy.utils

import android.content.Context
import android.widget.Toast
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo

class Utils {

    fun toast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createUserPreferences(c: Context, user: FirebaseUser){
        val prefs =
            c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

        prefs.putString("name", user?.displayName)
        prefs.putString("email", user?.email)
        prefs.putString("UID", user?.uid)
        prefs.apply()
    }

    fun getUserPreferences(c: Context): User {
        var user = User()

        val prefs =
            c.getSharedPreferences(c.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        user.userUID = prefs.getString("UID", "").toString()
        user.name = prefs.getString("name", "").toString()
        user.email = prefs.getString("email", "").toString()

        return user
    }
}