package com.example.android.foodrecipe.database.entities.main_category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    @ColumnInfo(name = "idcategory")
    val idCategory: String,
    @ColumnInfo(name = "strcategory")
    val strCategory: String,
    @ColumnInfo(name = "strcategorydescription")
    val strCategoryDescription: String,
    @ColumnInfo(name = "strcategorythumb")
    val strCategoryThumb: String
)