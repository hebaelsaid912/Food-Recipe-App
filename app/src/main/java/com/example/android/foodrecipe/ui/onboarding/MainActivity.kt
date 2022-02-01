package com.example.android.foodrecipe.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.android.foodrecipe.pojo.onboarding.OnboardingItems
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.databinding.ActivityMainBinding
import com.example.android.foodrecipe.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
   private lateinit var binding:ActivityMainBinding
   private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setOnboardingItems()
        setIndicators()
        setCurrentIndicator(0)
    }
    private fun setOnboardingItems(){
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItems(
                    imageResource = R.drawable.img1,
                    title = "Eat your Fav Food",
                    description = " Eat your Fav Food Eat your Fav Food Eat your Fav Food Eat your Fav Food"
                ),
                OnboardingItems(
                    imageResource = R.drawable.img2,
                    title = "Eat your Fav Food",
                    description = " Eat your Fav Food Eat your Fav Food Eat your Fav Food Eat your Fav Food"
                ),
                OnboardingItems(
                    imageResource = R.drawable.img3,
                    title = "Eat your Fav Food",
                    description = " Eat your Fav Food Eat your Fav Food Eat your Fav Food Eat your Fav Food"
                )
            )
        )
        binding.onboardingViewPager.adapter = onboardingItemsAdapter
        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (binding.onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        binding.nextImageView.setOnClickListener {
            if( binding.onboardingViewPager.currentItem+1 < onboardingItemsAdapter.itemCount){
                binding.onboardingViewPager.currentItem += 1
            }else{
                navigateToHomeActivity()
            }
        }
        binding.skipText.setOnClickListener {
            navigateToHomeActivity()
        }
        binding.getStartBtn.setOnClickListener {
            navigateToHomeActivity()
        }
    }
    private fun setIndicators(){
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams : LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_page
                    )
                )
                it.layoutParams = layoutParams
                binding.indicatorContainer.addView(it)
            }
        }

    }
    private fun setCurrentIndicator(position:Int){
        val childCount = binding.indicatorContainer.childCount
        for (i in 0 until childCount){
            val imageView = binding.indicatorContainer.getChildAt(i) as ImageView
            if( i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_page
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_page
                    )
                )
            }
        }
    }
    private fun navigateToHomeActivity(){
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }
}