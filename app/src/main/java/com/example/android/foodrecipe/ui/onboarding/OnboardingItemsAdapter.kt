package com.example.android.foodrecipe.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.pojo.onboarding.OnboardingItems

class OnboardingItemsAdapter(private val onboardingItems: List<OnboardingItems>):
RecyclerView.Adapter<OnboardingItemsAdapter.OnboardingItemsViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemsViewHolder {
       return OnboardingItemsViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.onboarding_item_container,
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: OnboardingItemsViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }
    inner class OnboardingItemsViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val imageOnboarding = view.findViewById<pl.droidsonroids.gif.GifImageView>(R.id.onboardingImage)
        private val titleTextOnboarding = view.findViewById<TextView>(R.id.titleText)
        private val descriptionTextOnboarding = view.findViewById<TextView>(R.id.descriptionText)

        fun bind(onboardingItems: OnboardingItems){
            imageOnboarding.setImageResource(onboardingItems.imageResource)
            titleTextOnboarding.text = onboardingItems.title
            descriptionTextOnboarding.text = onboardingItems.description
        }
    }
}