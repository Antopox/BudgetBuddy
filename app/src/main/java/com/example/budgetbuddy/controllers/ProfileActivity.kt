package com.example.budgetbuddy.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Actividad para mostrar el perfil del usuario
 * -Cambiar tanto el nombre como la foto de perfil.
 * -Borrar la cuenta (Borrará todos los datos guardados)
 * -Menu drawer para navegar entre las diferentes actividades
 */
class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private var userName: String? = null
    private lateinit var txtUsername: TextView
    private lateinit var etUsername: EditText
    private lateinit var btDeleteAccount: Button
    private lateinit var btSettingsProfile: ImageButton
    private lateinit var btSettingsProfileCheck: ImageButton
    private lateinit var btSettingsProfileImage: ImageButton
    private lateinit var profileImage: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        drawerLayout = findViewById(R.id.main_profile)

        val currentUser = FirebaseAuth.getInstance().currentUser

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                val txtDisplayName = drawerView.findViewById<TextView>(R.id.txtDisplayName)
                val txtDisplayEmail = drawerView.findViewById<TextView>(R.id.txtDisplayEmail)
                val userimg = drawerView.findViewById<ImageView>(R.id.imgDisplay)
                txtDisplayName.text = currentUser?.displayName
                txtDisplayEmail.text = currentUser?.email

                val photoUrl: String = currentUser?.photoUrl.toString()
                if (photoUrl.isNotEmpty()) {
                    Glide.with(applicationContext)
                        .load(photoUrl)
                        .placeholder(R.drawable.iconbg) // Imagen de marcador de posición mientras carga
                        .apply(RequestOptions().transform(CircleCrop()))
                        .error(R.drawable.iconbg) // Imagen de marcador de posición en caso de error
                        .into(userimg)
                }
            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        val toolbar = findViewById<Toolbar>(R.id.profileToolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view_profile)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null){
            navigationView.setCheckedItem(R.id.mb_home)
        }

        txtUsername = findViewById(R.id.txtUsername)
        etUsername = findViewById(R.id.etUsername)

        btSettingsProfile = findViewById(R.id.btn_profile_settings)
        btSettingsProfileCheck = findViewById(R.id.btn_profile_settings_check)

        btDeleteAccount = findViewById(R.id.btn_delete_account)
        btSettingsProfileImage = findViewById(R.id.btn_edit_image)

        val user = FirebaseAuth.getInstance().currentUser
        userName = user?.displayName
        if (userName == null) {
            val email = user?.email
            if (email != null) {
                userName = email.substringBefore("@")
                // Crear un UserProfileChangeRequest
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()

                // Actualizar el perfil del usuario
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Utils().toast(this, task.exception?.message.toString())
                        }
                    }
            }
        }
        // Carga la foto de perfil del usuario
        profileImage = findViewById(R.id.profile_image)
        if (user?.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(profileImage)
        } else {
            Glide.with(this)
                .load(R.drawable.iconbgdark)
                .into(profileImage)
        }

        // Se pone el nombre del usuario
        txtUsername.text = userName
        etUsername.setText(userName)

        // Cambia la visibilidad de los botones para poder usarlos y hacer cambios en el perfil
        btSettingsProfile.setOnClickListener {
            btSettingsProfile.visibility = View.GONE
            txtUsername.visibility = View.GONE
            etUsername.visibility = View.VISIBLE
            btSettingsProfileCheck.visibility = View.VISIBLE
            btSettingsProfileImage.visibility = View.VISIBLE
            btDeleteAccount.visibility = View.VISIBLE
        }

        // Al realizar cambios en el perfil se confirman al pulsar en el botón del icono check
        btSettingsProfileCheck.setOnClickListener {
            // Si el nombre es distinto lo actualiza y también muestra las demás opciones como
            // borrar cuenta, borrar historial o cambiar la foto de perfil
            if (etUsername.text.toString() != userName) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(etUsername.text.toString())
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Actualizar el valor de userName
                            userName = user.displayName
                            Utils().toast(this, getString(R.string.updated_profile))
                            txtUsername.text = userName
                            etUsername.setText(userName)
                            changeVisibility()
                        } else {
                            Utils().toast(this, task.exception?.message.toString())
                            changeVisibility()
                        }
                    }
            } else { changeVisibility() }
        }

        // Borra la cuenta permanentemente del usuario
        btDeleteAccount.setOnClickListener {

            BeautifulDialog.build(this)
                .title(getString(R.string.delete_account), titleColor = R.color.black)
                .description(getString(R.string.are_you_sure))
                .type(type = BeautifulDialog.TYPE.NONE)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = getString(R.string.yes), shouldIDismissOnClick = true) {
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseRealtime().deleteUser(user.uid)
                                Utils().toast(this, getString(R.string.deleted_account))
                                exit()
                            } else {
                                Utils().toast(this, task.exception?.message.toString())
                            }
                        }
                }
                .onNegative(text = getString(R.string.cancel)) {}
        }

        // Abre la galería para seleccionar una foto y actualizarla en el perfil
        btSettingsProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        /*
        Firebase.initialize(requireContext())
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )*/
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.mb_home -> changeActivity(MainActivity())

            R.id.mb_categories -> changeActivity(CategoriesActivity())

            R.id.mb_profile -> drawerLayout.closeDrawer(GravityCompat.START)

            R.id.mb_logout-> closeSession()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun changeActivity(activity: AppCompatActivity){
        val i = Intent(this, activity::class.java)
        startActivity(i)
    }

    private fun closeSession(){
        Utils().deleteUserPreferences(applicationContext)
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }
    private fun changeVisibility() {
        txtUsername.visibility = View.VISIBLE
        etUsername.visibility = View.GONE
        btSettingsProfileCheck.visibility = View.GONE
        btSettingsProfileImage.visibility = View.GONE
        btSettingsProfile.visibility = View.VISIBLE
        btDeleteAccount.visibility = View.GONE
    }

    // Cierra la sesión del usuario y borra los ajustes de ese inicio de sesión para evitar entrar directamente
    private fun exit() {
        val prefs = this
            .getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        // Navega de vuelta a la actividad de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        // Cargamos la foto de perfil
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(profileImage)
            }

            // Subir la imagen a Firebase Storage y luego actualizar la URL de la foto de perfil en Firebase Auth
            val storageRef = FirebaseStorage.getInstance().reference.child("profileImages")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val uploadTask = storageRef.putFile(selectedImageUri!!)

            // Cambia la foto
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()

                    FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Utils().toast(this, getString(R.string.pp_updated))
                            } else {
                                // Si da error vuelve a la foto que tenía anteriormente
                                Utils().toast(this, task.exception?.message.toString())
                                Glide.with(this)
                                    .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                                    .into(profileImage)
                            }
                        }
                }
            }.addOnFailureListener { Utils().toast(this, it.message.toString()) }
        }
    }
}