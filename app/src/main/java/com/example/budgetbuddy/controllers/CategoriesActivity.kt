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
import com.example.budgetbuddy.R
import com.example.budgetbuddy.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)

        drawerLayout = findViewById(R.id.main_categories)
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
                userimg.setImageURI(currentUser?.photoUrl)
            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        val toolbar = findViewById<Toolbar>(R.id.categoriesToolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view_categories)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null){
            navigationView.setCheckedItem(R.id.mb_home)
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.mb_home -> changeActivity(MainActivity())

            R.id.mb_categories -> drawerLayout.closeDrawer(GravityCompat.START)

            R.id.mb_settings -> changeActivity(SettingsActivity())

            R.id.mb_profile -> changeActivity(ProfileActivity())

            R.id.mb_logout-> closeSession()
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            changeActivity(MainActivity())
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