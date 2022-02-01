package com.example.android.foodrecipe.data

import android.util.Log
import com.example.android.foodrecipe.database.entities.main_category.MainCategoryItems
import com.example.android.foodrecipe.database.entities.meal_details.MealDetails
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainCategoryClient {
    private val apiURL = "https://www.themealdb.com/api/json/v1/1/"
    var retrofit: MainCategoryInterface = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(apiURL)
        .build()
        .create(MainCategoryInterface::class.java)

    suspend fun getCategoryData(): MainCategoryItems {
        return retrofit.getCategoriesData()
    }
    suspend fun filterCategoryData(filterName:String): MealsItems {
        return retrofit.filterCategoriesData(filterName)
    }
    suspend fun getMealDetailsData(idMeal:String): MealDetails {
        return retrofit.getMealDetailsData(idMeal.toInt())
    }
}