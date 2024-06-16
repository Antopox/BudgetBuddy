package com.example.budgetbuddy.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R

/**
 * Adaptador para mostrar los iconos en el diálogo de nueva categoría
 * @param iconList Lista de recursos de IDs de iconos
 */
class IconAdapter(private val iconList: List<Int>) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    var onItemClick : ((Int) -> Unit)? = null
    private var viewClicked : View? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val iconResId = iconList[position]
        holder.iconView.setImageResource(iconResId)

        holder.itemView.setOnClickListener {
            viewClicked?.setBackgroundColor(Color.TRANSPARENT)
            viewClicked = it
            it.setBackgroundColor(Color.GRAY)
            onItemClick?.invoke(iconResId)
        }
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconView: ImageView = itemView.findViewById(R.id.imageViewIcon)
    }
}