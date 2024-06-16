package com.example.budgetbuddy.controllers

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.Utils
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad de recuperación de contraseña a traves del correo asociado
 */
class ForgotPassActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btSendEmail: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_pass)

        etEmail = findViewById(R.id.etEmailrecover)
        btSendEmail = findViewById(R.id.btResetPass)
        auth = FirebaseAuth.getInstance()

        btSendEmail.setOnClickListener {
            val email = etEmail.text.trim().toString()

            if (email.isEmpty()) {
                etEmail.error = getString(R.string.required)
            }else{
                //Si el campo no esta vacio se envia el correo de recuperación
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Utils().toast(this, getString(R.string.email_sent))
                        finish()
                    }
                }
            }
        }
    }
}