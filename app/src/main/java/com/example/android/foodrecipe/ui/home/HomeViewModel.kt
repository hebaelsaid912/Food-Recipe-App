package com.example.android.foodrecipe.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.foodrecipe.data.MainCategoryClient
import com.example.android.foodrecipe.database.data.RecipesDatabase
import com.example.android.foodrecipe.database.entities.main_category.Category
import com.example.android.foodrecipe.database.entities.sub_category.Meal
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.ArrayList

class HomeViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<MainCategoryStateFromDB>(MainCategoryStateFromDB.Idle)
    val stateFromDB: StateFlow<MainCategoryStateFromDB> get() = _viewState
    private val _viewStateMealItems =
        MutableStateFlow<FilterCategoryStateFromAPI>(FilterCategoryStateFromAPI.Idle)
    val stateFromDBMeal: StateFlow<FilterCategoryStateFromAPI> get() = _viewStateMealItems
    private  var mainCatigoryList= ArrayList<Category>()
    private  var subCatigoryList= ArrayList<Meal>()

    sealed class MainCategoryStateFromDB {
        data class Success(val categoryItemsList: ArrayList<Category>) : MainCategoryStateFromDB()
        data class Error(val message: String) : MainCategoryStateFromDB()
        object Loading : MainCategoryStateFromDB()
        object Idle : MainCategoryStateFromDB()
    }

    sealed class FilterCategoryStateFromAPI {
        data class Success(val mealsItems: MealsItems) : FilterCategoryStateFromAPI()
        data class Error(val message: String) : FilterCategoryStateFromAPI()
        object Loading : FilterCategoryStateFromAPI()
        object Idle : FilterCategoryStateFromAPI()
    }

    fun getCategoriesFromDB(context: Context) {
        viewModelScope.launch {
            _viewState.value = MainCategoryStateFromDB.Loading
            getDataFromDb(context)
            delay(2000L)
            _viewState.value = try {
                MainCategoryStateFromDB.Success(mainCatigoryList)
            } catch (ex: Exception) {
                MainCategoryStateFromDB.Error(ex.message!!)
            }
        }
    }
    private fun getDataFromDb(context: Context) = viewModelScope.launch(Dispatchers.Default) {
        val categoryItemsList = RecipesDatabase
            .getDatabase(context).recipeDao().getAllCategory
        mainCatigoryList = categoryItemsList as ArrayList<Category>
        mainCatigoryList.reverse()
    }

    fun getMealsFromDB(context: Context, filterName: String) {
        viewModelScope.launch {
            _viewStateMealItems.value = FilterCategoryStateFromAPI.Loading
            getMealDataFromDb(context,filterName)
            delay(2000L)
            _viewStateMealItems.value = try {
                FilterCategoryStateFromAPI.Success(MealsItems(subCatigoryList))
            } catch (ex: Exception) {
                Log.d("MainActivity","Error Category ${ex.message!!}")
                FilterCategoryStateFromAPI.Error(ex.message!!)
            }
        }
    }

    private fun getMealDataFromDb(context: Context,filterName: String) =
        viewModelScope.launch(Dispatchers.Default) {
                val mealCategoryItemsList = RecipesDatabase
                    .getDatabase(context).recipeDao().getCategoryMeals(filterName)
                subCatigoryList = mealCategoryItemsList as ArrayList<Meal>
            subCatigoryList.reverse()
        }


}