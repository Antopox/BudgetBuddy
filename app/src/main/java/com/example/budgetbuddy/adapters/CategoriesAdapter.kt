package com.example.budgetbuddy.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.models.Category
import de.hdodenhof.circleimageview.CircleImageView


class CategoriesAdapter(pcategories : ArrayList<Category>) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>(){

    val categories : ArrayList<Category>
    lateinit var context : Context
     var viewClicked : View? = null

    var onItemClick : ((Category) -> Unit)? = null

    init {
        this.categories = pcategories
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.category_custom_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategoriesAdapter.ViewHolder, position: Int) {
        val cat = categories[position]

        holder.txtName.text = cat.name
        holder.imgCat.circleBackgroundColor = Color.parseColor(cat.bgcolor)
        val idimg = context.resources.getIdentifier(cat.icon, "drawable", context.packageName)

        holder.imgCat.setImageResource(idimg)

        holder.itemView.setOnClickListener{

            viewClicked?.setBackgroundColor(Color.TRANSPARENT)
            viewClicked = it
            it.setBackgroundColor(Color.GRAY)
            onItemClick?.invoke(cat)
        }
    }

    override fun getItemCount(): Int {
        return this.categories.size
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var txtName : TextView
        var imgCat : CircleImageView

        init {
            txtName = itemView.findViewById(R.id.txtCatName)
            imgCat = itemView.findViewById(R.id.imgCategory)
        }
    }
}