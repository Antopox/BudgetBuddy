package com.example.budgetbuddy.controllers

import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.CategoriesAdapter
import com.example.budgetbuddy.models.Category
import com.example.budgetbuddy.utils.FirebaseRealtime
import com.example.budgetbuddy.utils.NewCategoryDialog
import com.example.budgetbuddy.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, FirebaseRealtime.FirebaseCategoriesCallback {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var recviewCat : RecyclerView
    private lateinit var adapter: CategoriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)

        recviewCat = findViewById(R.id.recViewCategories)

        recviewCat.layoutManager = GridLayoutManager(this, 3)

        drawerLayout = findViewById(R.id.main_categories)
        val currentUser = FirebaseAuth.getInstance().currentUser

        FirebaseRealtime().getCategories(Utils().getUserUID(this), this)

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
                if (!photoUrl.isNullOrEmpty()) {
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_category, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btAddNewCategory -> {
                NewCategoryDialog (
                    onSubmitClickListener = {
                        FirebaseRealtime().addCategory(Utils().getUserUID(this), it)
                    }
                ).show(supportFragmentManager, "NewRecordDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onCategoriesLoaded(cats: ArrayList<Category>) {
        adapter = CategoriesAdapter(cats)
        recviewCat.adapter = adapter
    }
}