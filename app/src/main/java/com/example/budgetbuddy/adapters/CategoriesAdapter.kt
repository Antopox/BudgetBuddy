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

/**
 * Adatador de categorias
 * @param pcategories lista de categorias
 */
class CategoriesAdapter(pcategories : ArrayList<Category>) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>(){

    private val categories : ArrayList<Category> = pcategories
    lateinit var context : Context
    private var viewClicked : View? = null

    var onItemClick : ((Category) -> Unit)? = null
    lateinit var onItemLongClick : ((Category, View) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.category_custom_layout, parent, false)
        return ViewHolder(v)
    }

    /**
     * Setea los datos de la categoria y se asigna un click listener y un long click listener
     * usando los atributos onItemClick y onItemLongClick(métodos que se añaden en la activity)
     */
    override fun onBindViewHolder(holder: CategoriesAdapter.ViewHolder, position: Int) {
        val cat = categories[position]

        holder.txtName.text = cat.name


        if (cat.icon == 0){
            holder.imgCat.setImageResource(R.drawable.iconbgdark)
        }else{
            holder.imgCat.setImageResource(cat.icon)
        }

        holder.imgCat.circleBackgroundColor = Color.parseColor("#" + cat.bgcolor)

        holder.itemView.setOnClickListener{

            viewClicked?.setBackgroundColor(Color.TRANSPARENT)
            viewClicked = it
            it.setBackgroundColor(Color.GRAY)
            onItemClick?.invoke(cat)
        }
        holder.itemView.setOnLongClickListener{
            onItemLongClick(cat, it)
            true
        }
    }

    override fun getItemCount(): Int {
        return this.categories.size
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var txtName : TextView = itemView.findViewById(R.id.txtCatName)
        var imgCat : CircleImageView = itemView.findViewById(R.id.imgCategory)
    }
}