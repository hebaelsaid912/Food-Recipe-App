package com.example.android.foodrecipe.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.foodrecipe.database.dao.RecipesDao
import com.example.android.foodrecipe.database.entities.main_category.Category
import com.example.android.foodrecipe.database.entities.meal_details.MealData
import com.example.android.foodrecipe.database.entities.sub_category.Meal
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems


@Database(entities = [Category::class , Meal::class, MealData::class],
    version = 1 , exportSchema = false)
abstract class RecipesDatabase : RoomDatabase(){

    companion object{
        var recipesDatabase:RecipesDatabase? = null

        @Synchronized
        fun getDatabase(context: Context) : RecipesDatabase{
            if( recipesDatabase == null){
                recipesDatabase = Room.databaseBuilder(
                    context,
                    RecipesDatabase::class.java,
                    "recipe.db"
                ).build()
            }
            return recipesDatabase!!
        }

    }
    abstract fun recipeDao():RecipesDao
}
