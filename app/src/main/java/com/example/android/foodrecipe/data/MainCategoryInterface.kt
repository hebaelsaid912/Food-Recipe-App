package com.example.android.foodrecipe.data

import com.example.android.foodrecipe.database.entities.main_category.MainCategoryItems
import com.example.android.foodrecipe.database.entities.meal_details.MealDetails
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems
import retrofit2.http.GET
import retrofit2.http.Query

interface MainCategoryInterface {
    @GET("categories.php")
    suspend fun getCategoriesData(): MainCategoryItems

    @GET("filter.php")
    suspend fun filterCategoriesData(@Query("c") filterName: String): MealsItems

    @GET("lookup.php")
    suspend fun getMealDetailsData(@Query("i") idMeal: Int): MealDetails
}