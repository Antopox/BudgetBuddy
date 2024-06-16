package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Actividad para el inicio de sesión.
 * Inicio con correo y contraseña.
 * Inicio con Google.
 * Registrate. -> SignInActivity
 * He olvidado mi contraseña. -> ForgotPassActivity
 */
class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100

    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var btLogin: Button
    private lateinit var btGooogleLogin: Button
    private lateinit var btSignIn: Button

    private lateinit var forgotPass: TextView
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        session()

        forgotPass = findViewById(R.id.txtForgotPass)
        txtEmail = findViewById(R.id.etLoginEmail)
        txtPass = findViewById(R.id.etLoginPass)
        btLogin = findViewById(R.id.btLogIn)
        btSignIn = findViewById(R.id.btSignIn)
        btGooogleLogin = findViewById(R.id.btGoogleLogin)

        auth = FirebaseAuth.getInstance()
        btLogin.setOnClickListener {
            if (checkValues()){
                auth.signInWithEmailAndPassword(txtEmail.text.toString().trim(), txtPass.text.toString().trim()).addOnSuccessListener {
                    Utils().toast(applicationContext, getString(R.string.login_success))
                    toMainActivity()
                    this.finish()
                }.addOnFailureListener {
                    Utils().toast(applicationContext, it.message.toString())
                }
            }
        }

        forgotPass.setOnClickListener {
            val i = Intent(this, ForgotPassActivity::class.java)
            startActivity(i)
        }

        btSignIn.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }

        btGooogleLogin.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()
        session()
    }

    // Verifica que los EditText no estén vacíos
    private fun checkValues(): Boolean{

        if (txtEmail.text.isEmpty()){
            txtEmail.error = getString(R.string.required)
            return false
        }else if (txtPass.text.isEmpty()){
            txtPass.error = getString(R.string.required)
            return false
        }else{
            return true
        }
    }

    // Verifica que el usuario esté logueado, en cuyo caso se redirige a MainActivity
    private fun session(){
        if (Utils().getIsSignedIn(applicationContext)){
            toMainActivity()
            this.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null){
                val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credentials)

                toMainActivity()
            }

        }
    }

    private fun toMainActivity(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressedDispatcher.onBackPressed()
    }
}