package com.example.android.foodrecipe.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.database.entities.main_category.Category
import java.net.URL

class MainAdapter(private var mainCategoryItems : ArrayList<Category>)
    : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    private lateinit var context: Context
    var listener:OnItemClickListener?=null
    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.main_item_rv,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(mainCategoryItems[position])
        holder.itemView.setOnClickListener {
            listener!!.onClick(mainCategoryItems[position].strCategory)
        }
    }

    override fun getItemCount(): Int {
        return mainCategoryItems.size
    }
    inner class MainViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val dishImage = view.findViewById<ImageView>(R.id.img_dish)
        val dishNameText = view.findViewById<TextView>(R.id.tv_dish_name)
        fun bind(mainCategoryItem: Category){
            dishNameText.text = mainCategoryItem.strCategory
            val link = URL(mainCategoryItem.strCategoryThumb)
            val url = GlideUrl(link)
            Glide.with(context)
                .load(url)
                .error(R.drawable.error_internet_connection)
                .into(dishImage)

        }
    }

    interface OnItemClickListener{
        fun onClick(categoryStr:String)
    }
}