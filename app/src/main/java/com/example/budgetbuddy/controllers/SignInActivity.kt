package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Actividad de registro de usuarios en Firebase Authentication
 * mediante un nombre, un correo electrónico y una contraseña.
 */
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

    private fun checkValues(): Boolean{
        if (txtUserName.text.toString().trim() == ""){
            txtUserName.error = getString(R.string.required)
            return false
        }else if (txtUserEmail.text.toString().trim() == ""){
            txtUserEmail.error = getString(R.string.required)
            return false
        }else if (txtUserPass.text.toString().trim() == ""){
            txtUserPass.error = getString(R.string.required)
            return false
        }else{
            return true
        }
    }
}