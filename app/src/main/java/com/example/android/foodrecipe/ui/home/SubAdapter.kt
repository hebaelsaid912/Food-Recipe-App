package com.example.android.foodrecipe.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.database.entities.sub_category.Meal
import java.net.URL

class SubAdapter(private val subCategoryItems:List<Meal>)
    : RecyclerView.Adapter<SubAdapter.MainViewHolder>() {
    private lateinit var context: Context
    var listener: OnItemClickListener?=null
    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.sub_item_rv,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(subCategoryItems[position])
        holder.itemView.setOnClickListener {
            listener!!.onClick(subCategoryItems[position])
        }
    }

    override fun getItemCount(): Int {
        return subCategoryItems.size
    }
    inner class MainViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val dishImage = view.findViewById<ImageView>(R.id.dishImage)
        val dishNameText = view.findViewById<TextView>(R.id.dishNameTV)
        fun bind(subCategoryItem: Meal){
            dishNameText.text = subCategoryItem.strMeal
            val link = URL(subCategoryItem.strMealThumb)
            val url = GlideUrl(link)
            Glide.with(context)
                .load(url)
                .error(R.drawable.error_internet_connection)
                .into(dishImage)
        }
    }
    interface OnItemClickListener{
        fun onClick(meal: Meal)
    }

}