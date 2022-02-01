package com.example.android.foodrecipe.database.entities.sub_category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "idMeal")
    val idMeal: String,
    @ColumnInfo(name = "categoryName")
    val categoryName: String,
    @ColumnInfo(name = "strMeal")
    val strMeal: String,
    @ColumnInfo(name = "strMealThumb")
    val strMealThumb: String,
)