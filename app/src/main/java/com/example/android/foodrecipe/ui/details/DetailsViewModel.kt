package com.example.android.foodrecipe.ui.details

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.foodrecipe.data.MainCategoryClient
import com.example.android.foodrecipe.database.data.RecipesDatabase
import com.example.android.foodrecipe.database.entities.meal_details.MealData
import com.example.android.foodrecipe.database.entities.meal_details.MealDetails
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel: ViewModel() {
    private val _viewState = MutableStateFlow<MealDetailsStateFromAPI>(MealDetailsStateFromAPI.Idle)
    val stateFromAPI: StateFlow<MealDetailsStateFromAPI> get() = _viewState
    private val _viewStateMealDetails =
        MutableStateFlow<MealDetailsStateFromDB>(MealDetailsStateFromDB.Idle)
    val stateFromDBMeal: StateFlow<MealDetailsStateFromDB> get() = _viewStateMealDetails
    private lateinit var mealDetailsDataFromDB : MealData
    private val recipesClient: MainCategoryClient by lazy {
        MainCategoryClient()
    }
    sealed class MealDetailsStateFromAPI{
        data class Success(val mealDetails: MealDetails): MealDetailsStateFromAPI()
        data class Error(val message: String) : MealDetailsStateFromAPI()
        object Loading: MealDetailsStateFromAPI()
        object Idle: MealDetailsStateFromAPI()
    }

    sealed class MealDetailsStateFromDB {
        data class Success(val mealDetails: MealData) : MealDetailsStateFromDB()
        data class Error(val message: String) : MealDetailsStateFromDB()
        object Loading : MealDetailsStateFromDB()
        object Idle : MealDetailsStateFromDB()
    }


     fun getMealsDetails(idMeal:String) = viewModelScope.launch {
        _viewState.value = MealDetailsStateFromAPI.Loading
        //delay(2000)
        _viewState.value = try {
            MealDetailsStateFromAPI.Success(getDetails(idMeal))

        }catch (ex : Exception){
            MealDetailsStateFromAPI.Error(ex.message!!)
        }
    }

    private fun getDetails(idMeal: String): MealDetails = runBlocking{
        recipesClient.getMealDetailsData(idMeal)
    }

    fun getMealDetailsFromDB(context: Context, idMeal: String) {
        viewModelScope.launch {
            _viewStateMealDetails.value = MealDetailsStateFromDB.Loading
            getMealDetailsFromDb(context,idMeal)
            delay(2000L)
            _viewStateMealDetails.value = try {
                MealDetailsStateFromDB.Success(mealDetailsDataFromDB)
            } catch (ex: Exception) {
                Log.d("DetailsViewModel","Error Details ${ex.message!!}")
                MealDetailsStateFromDB.Error(ex.message!!)
            }
        }
    }

    private fun getMealDetailsFromDb(context: Context, idMeal: String) =
        viewModelScope.launch  (Dispatchers.Default) {
            Log.d("DetailsViewModel","getMealDetailsFromDb Details main $idMeal")
            val main = RecipesDatabase
                .getDatabase(context).recipeDao().getMealDetails(idMeal)
            mealDetailsDataFromDB = main
            Log.d("DetailsViewModel","getMealDetailsFromDb Details main $main")
            Log.d("DetailsViewModel","getMealDetailsFromDb Details $mealDetailsDataFromDB")
        }

}