package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignInActivity : AppCompatActivity() {

    private lateinit var txtUserName: EditText
    private lateinit var txtUserEmail: EditText
    private lateinit var txtUserPass: EditText
    private lateinit var btRegister: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        txtUserName = findViewById(R.id.etRegisterName)
        txtUserEmail = findViewById(R.id.etRegisterEmail)
        txtUserPass = findViewById(R.id.etRegisterPass)
        btRegister = findViewById(R.id.btRegisterUser)

        btRegister.setOnClickListener {
            if (checkValues()){
                auth.createUserWithEmailAndPassword(txtUserEmail.text.toString(), txtUserPass.text.toString()).addOnSuccessListener {
                    val user = it.user
                    // Actualiza el perfil del usuario con el nombre proporcionado
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(txtUserName.text.toString()).build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                val i = Intent(this, MainActivity::class.java)
                                startActivity(i)
                            }
                        }
                }
            }
        }
    }

    fun checkValues(): Boolean{
        if (txtUserName.text.toString().trim() == ""){
            txtUserName.setError(getString(R.string.required))
            return false
        }else if (txtUserEmail.text.toString().trim() == ""){
            txtUserEmail.setError(getString(R.string.required))
            return false
        }else if (txtUserPass.text.toString().trim() == ""){
            txtUserPass.setError(getString(R.string.required))
            return false
        }else{
            return true
        }
    }
}