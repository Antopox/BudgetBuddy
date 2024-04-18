package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy.R

class SplashScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splashscreen)
        val img = findViewById<ImageView>(R.id.splashLogo)
        val theme = obtainStyledAttributes(intArrayOf(android.R.attr.isLightTheme))
        val isLightTheme = theme.getBoolean(0, false)
        theme.recycle()

        if (isLightTheme) {
            img.setImageResource(R.drawable.iconbg)
        } else {
            img.setImageResource(R.drawable.iconbgdark)
        }
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual para que no se pueda volver atrás
        }, 5000)
    }
}