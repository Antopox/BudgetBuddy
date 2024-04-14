package com.example.budgetbuddy.controllers

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.R

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var btLogin: Button
    private lateinit var btGooogleLogin: Button
    private lateinit var btFaceLogin: Button
    private lateinit var btXlogin: Button
    private lateinit var btSignup: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtEmail = findViewById(R.id.etUserEmail)
        txtPass = findViewById(R.id.etUserPass)
        btLogin = findViewById(R.id.btLogIn)
        btSignup = findViewById(R.id.btSignUp)
        btGooogleLogin = findViewById(R.id.btGoogleLogin)
        btFaceLogin = findViewById(R.id.btFacebookLogin)
        btXlogin = findViewById(R.id.btTwitterLogin)

    }
}