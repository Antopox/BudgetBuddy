package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityMainBinding
import com.example.budgetbuddy.fragments.BalanceFragment
import com.example.budgetbuddy.fragments.CalendarFragment
import com.example.budgetbuddy.fragments.RecordFragment
import com.example.budgetbuddy.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad principal con 3 pantallas disponibles mediante un BottomNavigationView
 * BalanceFragment -> Muestra tu balance y un gráfico con tus gastos e ingresos
 * RecordsFragment -> Muestra tu historial de gastos y ingresos
 * CalendarFragment -> Muestra un calendario con tus gastos e ingresos
 * Menu drawer para navegar entre las actividades
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = findViewById(R.id.main)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (!Utils().getIsSignedIn(applicationContext) && currentUser != null){
            Utils().createUserPreferences(applicationContext, currentUser)
        }

        // Al abrir el menu se carga la información del usuario
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

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.balance)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null){
            replaceFragment(BalanceFragment())
            navigationView.setCheckedItem(R.id.mb_home)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.mb_balance -> {
                    replaceFragment(BalanceFragment())
                    supportActionBar?.title = getString(R.string.balance)
                }
                R.id.mb_record -> {
                    replaceFragment(RecordFragment())
                    supportActionBar?.title = getString(R.string.record)
                }
                R.id.mb_calendar -> {
                    replaceFragment(CalendarFragment())
                    supportActionBar?.title = getString(R.string.calendar)
                }

                else ->{}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.mb_home -> drawerLayout.closeDrawer(GravityCompat.START)

            R.id.mb_categories -> changeActivity(CategoriesActivity())

            R.id.mb_profile -> changeActivity(ProfileActivity())

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
}