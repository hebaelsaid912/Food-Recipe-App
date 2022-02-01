package com.example.android.foodrecipe.ui.details

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.example.android.foodrecipe.R
import java.net.URL

class MealDetailsAdapter(private val strIngredientList:List<String>,private val strIngredientImageList:List<String>)
    : RecyclerView.Adapter<MealDetailsAdapter.MealDetailsViewHolder>() {
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDetailsViewHolder {
        context = parent.context
        return MealDetailsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.details_items_component_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MealDetailsViewHolder, position: Int) {
            holder.bind(strIngredientList[position], strIngredientImageList[position])

    }

    override fun getItemCount(): Int {
        return strIngredientList.size
    }
    inner class MealDetailsViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.img_component)
        val nameText = view.findViewById<TextView>(R.id.tv_component_name)
        fun bind(strIngredient: String , imageString: String){
            if(strIngredient.isNotBlank() && strIngredientList.isNotEmpty()) {
                nameText.text = strIngredient
                val link = URL(imageString)
                val url = GlideUrl(link)
                Glide.with(context)
                    .load(url)
                    .error(R.drawable.error_internet_connection)
                    .into(image)
            }else{
                itemView.layoutParams.height =0
                itemView.layoutParams.width =0
            }
        }
    }

}