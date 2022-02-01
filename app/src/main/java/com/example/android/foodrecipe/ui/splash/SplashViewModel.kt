package com.example.android.foodrecipe.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.foodrecipe.data.MainCategoryClient
import com.example.android.foodrecipe.database.entities.main_category.MainCategoryItems
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<MainCategoryStateFromAPI>(MainCategoryStateFromAPI.Idle)
    val stateFromAPI: StateFlow<MainCategoryStateFromAPI> get() = _viewState

    private val _viewStateMeal = MutableStateFlow<FilterCategoryState>(FilterCategoryState.Idle)
    val stateMeal: StateFlow<FilterCategoryState> get() = _viewStateMeal


    private val recipesClient: MainCategoryClient by lazy {
        MainCategoryClient()
    }

    sealed class MainCategoryStateFromAPI{
        data class Success(val categoryItems: MainCategoryItems): MainCategoryStateFromAPI()
        data class Error(val message: String) : MainCategoryStateFromAPI()
        object Loading: MainCategoryStateFromAPI()
        object Idle: MainCategoryStateFromAPI()
    }
    sealed class FilterCategoryState{
        data class Success(val mealsItems: MealsItems,val categoryName:String): FilterCategoryState()
        data class Error(val message: String) : FilterCategoryState()
        object Loading: FilterCategoryState()
        object Idle: FilterCategoryState()
    }
    init {
        getCategories()
    }

    private fun getCategories() = viewModelScope.launch {
        _viewState.value = MainCategoryStateFromAPI.Loading
        delay(2000L)
        _viewState.value = try {
            MainCategoryStateFromAPI.Success(getCategoriesData())
        }catch (ex : Exception){
            MainCategoryStateFromAPI.Error(ex.message!!)
        }
    }

    private fun getCategoriesData() : MainCategoryItems = runBlocking{
       // Log.d("SplashViewModel","${recipesClient.getCategoryData()}")
        recipesClient.getCategoryData()
    }

    fun getMeals(filterName : String) = viewModelScope.launch {
        _viewStateMeal.value = FilterCategoryState.Loading
        delay(2000L)
        _viewStateMeal.value = try {
            FilterCategoryState.Success(getMealsData(filterName),filterName)
        }catch (ex : Exception){
            FilterCategoryState.Error(ex.message!!)
        }
    }

    private fun getMealsData(filterName : String) : MealsItems = runBlocking{
        // Log.d("SplashViewModel","${recipesClient.getCategoryData()}")
        recipesClient.filterCategoryData(filterName)
    }
}