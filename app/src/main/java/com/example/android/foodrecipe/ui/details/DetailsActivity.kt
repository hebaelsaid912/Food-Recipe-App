package com.example.android.foodrecipe.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.database.data.RecipesDatabase
import com.example.android.foodrecipe.database.entities.main_category.MainCategoryItems
import com.example.android.foodrecipe.database.entities.meal_details.MealData
import com.example.android.foodrecipe.database.entities.meal_details.MealDetails
import com.example.android.foodrecipe.databinding.ActivityDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URL

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val imageLink = "https://www.themealdb.com/images/ingredients/"
    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this)[DetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        val mealName = intent.getStringExtra("strMeal")
        val mealImageURLString = intent.getStringExtra("strMealImage")
        val mealId = intent.getStringExtra("idMeal")
        lifecycleScope.launch {
                if (mealId != null) {
                    processRender(mealId)
                }
        }

        binding.itemTitleTv.text = mealName
        val link = URL(mealImageURLString)
        val url = GlideUrl(link)
        Glide.with(this)
            .load(url)
            .error(R.drawable.error_internet_connection)
            .into(binding.imageItemView)

    }

    private fun processRender(idMeal: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.getMealsDetails(idMeal)
            viewModel.stateFromAPI.collect { mealDetails ->
                when (mealDetails) {
                    is DetailsViewModel.MealDetailsStateFromAPI.Success -> {
                        setViewsData(mealDetails.mealDetails.meals[0])
                        //insertRecipeDataIntoRoomDB(mealDetails = mealDetails.mealDetails)
                    }
                    is DetailsViewModel.MealDetailsStateFromAPI.Loading -> {
                        Log.d("DetailsActivity", "Loading meal details from API")
                    }
                    is DetailsViewModel.MealDetailsStateFromAPI.Error -> {
                        Log.d("DetailsActivity", "Error loading meal details ${mealDetails.message}")
                    }
                    else -> Unit
                }

            }

        }
    }

    private fun processCategoryItems(idMeal: String) {
        lifecycleScope.launch {
            viewModel.getMealDetailsFromDB(this@DetailsActivity, idMeal)
            delay(2000L)
            viewModel.stateFromDBMeal.collect { mealDetails ->
                when (mealDetails) {
                    is DetailsViewModel.MealDetailsStateFromDB.Success -> {
                        setViewsData(mealDetails.mealDetails)
                        Log.d("DetailsActivity", "Success setViewsData")
                    }
                    is DetailsViewModel.MealDetailsStateFromDB.Loading -> {
                        Log.d("DetailsActivity", "Loading meal details from database")
                    }
                    is DetailsViewModel.MealDetailsStateFromDB.Error -> {
                        Log.d("DetailsActivity", "Error meal details from database ${mealDetails.message}")
                    }
                    else -> Unit
                }
            }

        }
    }

    private fun setViewsData(mealDetails: MealData) {
        val list = ArrayList<String>()
        mealDetails.strIngredient1?.let { list.add(it) }
        mealDetails.strIngredient2?.let { list.add(it) }
        mealDetails.strIngredient3?.let { list.add(it) }
        mealDetails.strIngredient4?.let { list.add(it) }
        mealDetails.strIngredient5?.let { list.add(it) }
        mealDetails.strIngredient6?.let { list.add(it) }
        mealDetails.strIngredient7?.let { list.add(it) }
        mealDetails.strIngredient8?.let { list.add(it) }
        mealDetails.strIngredient9?.let { list.add(it) }
        mealDetails.strIngredient10?.let { list.add(it) }
        mealDetails.strIngredient11?.let { list.add(it) }
        mealDetails.strIngredient12?.let { list.add(it) }
        mealDetails.strIngredient13?.let { list.add(it) }
        mealDetails.strIngredient14?.let { list.add(it) }
        mealDetails.strIngredient15?.let { list.add(it) }
        mealDetails.strIngredient16?.let { list.add(it) }
        mealDetails.strIngredient17?.let { list.add(it) }
        mealDetails.strIngredient18?.let { list.add(it) }
        mealDetails.strIngredient19?.let { list.add(it) }
        mealDetails.strIngredient20?.let { list.add(it) }
        val strIngredientList = ArrayList<String>()
        val strIngredientImageList = ArrayList<String>()
        for (item in list){
            strIngredientList.add(item)
            strIngredientImageList.add("$imageLink$item-Small.png")
        }
        for (item in 0 until list.size-1){
            Log.d("DetailsActivity","${strIngredientList[item]} xxx ${strIngredientImageList[item]}")
        }
        val detailsAdapter = MealDetailsAdapter(strIngredientList,strIngredientImageList)
        binding.componentItemsRV.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.componentItemsRV.adapter = detailsAdapter
    }

   private fun insertRecipeDataIntoRoomDB(mealDetails: MealDetails) {
        lifecycleScope.launch(Dispatchers.Default) {
            this.let {
                clearMealDetailsDatabase()
                for (element in mealDetails.meals) {
                    Log.d("DetailsActivity","insertRecipeDataIntoRoomDB $element")
                   val xxx =  RecipesDatabase.getDatabase(this@DetailsActivity)
                        .recipeDao().insertMealDetails(element)
                    Log.d("DetailsViewModel","insertRecipeDataIntoRoomDB Details xxx $xxx")
                }

            }
        }
    }

    private fun clearMealDetailsDatabase() {
        lifecycleScope.launch(Dispatchers.Default) {
            this.let {
                RecipesDatabase.getDatabase(this@DetailsActivity).recipeDao()
                    .clearMealDetailsDatabase()
            }
        }
    }
}