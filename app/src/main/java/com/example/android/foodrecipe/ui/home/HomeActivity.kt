package com.example.android.foodrecipe.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.databinding.ActivityHomeBinding
import com.example.android.foodrecipe.database.entities.main_category.Category
import com.example.android.foodrecipe.database.entities.sub_category.Meal
import com.example.android.foodrecipe.ui.details.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var subAdapter: SubAdapter
    private lateinit var mainCategoryList: ArrayList<Category>
    private  var subCategoryList= ArrayList<Meal>()

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        processCategoryItems()
        binding.emptyView.visibility = View.VISIBLE
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(p0: String?): Boolean {
                val tempArr = ArrayList<Meal>()
                if(!subCategoryList.isNullOrEmpty()) {
                    for (arr in subCategoryList) {
                        if (arr.strMeal.lowercase(Locale.getDefault()).contains(p0.toString())) {
                            tempArr.add(arr)
                            Log.d(
                                "HomeActivity",
                                "onQueryTextChange ${arr.strMeal.lowercase(Locale.getDefault())}"
                            )
                        }
                    }
                    subAdapter = SubAdapter(tempArr)
                    subAdapter.notifyDataSetChanged()
                    binding.rvSubCategory.adapter = subAdapter
                }
                return true
            }

        })
    }

    private fun processCategoryItems() {
        lifecycleScope.launch {
            viewModel.getCategoriesFromDB(this@HomeActivity)
            viewModel.stateFromDB.collect { categoryItems ->
                when (categoryItems) {
                    is HomeViewModel.MainCategoryStateFromDB.Success -> {
                        binding.shimmerEffectRV.visibility = View.GONE
                        binding.shimmerEffectRV.stopShimmer()
                        mainCategoryList = categoryItems.categoryItemsList
                        mainAdapter = MainAdapter(mainCategoryList)
                        setMainCategoryDate(mainAdapter)
                        for (i in 0 until mainCategoryList.size) {
                            Log.d(
                                "HomeActivity",
                                "From processCategoryItems ${mainCategoryList[i].strCategory}"
                            )
                        }
                        Log.d("HomeActivity", "Get Category From DB")
                    }
                    is HomeViewModel.MainCategoryStateFromDB.Loading -> {
                        binding.shimmerEffectRV.visibility = View.VISIBLE
                        binding.shimmerEffectRV.startShimmer()
                        Log.d("HomeActivity", "Loading Category Data")
                            /*Toast.makeText(
                            this@HomeActivity,
                            "Loading Category Data",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    is HomeViewModel.MainCategoryStateFromDB.Error -> {
                        Log.d("HomeActivity", "Error's ${categoryItems.message}")
                        /*Toast.makeText(this@HomeActivity, "Error Category Data", Toast.LENGTH_SHORT)
                            .show()*/
                    }
                    else -> Unit
                }
            }
        }
    }

    private val onClickedCategoryItems = object : MainAdapter.OnItemClickListener {
        override fun onClick(categoryStr: String) {
            Log.d("HomeActivity", "Category clicked is $categoryStr")
            binding.tvCategory.text = categoryStr
            binding.rvSubCategory.visibility = View.GONE
            binding.rvSubCategory.adapter = null
            binding.shimmerEffectItemsRV.visibility = View.VISIBLE
            binding.shimmerEffectItemsRV.startShimmer()
            binding.emptyView.visibility = View.GONE
            processFilterMealsFromDB(categoryStr)

        }
    }
    private val onClickedMealItems = object : SubAdapter.OnItemClickListener {
        override fun onClick(meal:Meal) {
            Log.d("HomeActivity", "Meal clicked is ${meal.strMeal}")
            val intent = Intent(this@HomeActivity,DetailsActivity::class.java)
            intent.putExtra("idMeal" , meal.idMeal)
            intent.putExtra("strMeal" , meal.strMeal)
            intent.putExtra("strMealImage" , meal.strMealThumb)
            startActivity(intent)

        }
    }

    private fun processFilterMealsFromDB(filterName: String) {
        Log.d("HomeActivity", filterName)
        lifecycleScope.launch {
            viewModel.getMealsFromDB(this@HomeActivity, filterName)
            viewModel.stateFromDBMeal.collect { filterMeal ->
                when (filterMeal) {
                    is HomeViewModel.FilterCategoryStateFromAPI.Success -> {
                        binding.rvSubCategory.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                        binding.shimmerEffectItemsRV.visibility = View.GONE
                        binding.shimmerEffectItemsRV.stopShimmer()
                        subCategoryList = filterMeal.mealsItems.meals as ArrayList<Meal>
                        subAdapter = SubAdapter(subCategoryList)
                        subAdapter.setOnClickListener(onClickedMealItems)
                        setSubCategoryDate(subAdapter)
                        Log.d("HomeActivity", filterMeal.mealsItems.toString())
                    }
                    is HomeViewModel.FilterCategoryStateFromAPI.Loading -> {
                        binding.rvSubCategory.visibility = View.GONE
                        binding.rvSubCategory.adapter = null
                        binding.emptyView.visibility = View.GONE
                        binding.shimmerEffectItemsRV.visibility = View.VISIBLE
                        binding.shimmerEffectItemsRV.startShimmer()
                        Log.d("HomeActivity", "Loading Meals Data")
                        /*Toast.makeText(
                            this@HomeActivity,
                            "Loading Meals Data",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    is HomeViewModel.FilterCategoryStateFromAPI.Error -> {
                        Log.d("HomeActivity", "Error Meals Data")
                        /*Toast.makeText(this@HomeActivity, "Error Meals Data", Toast.LENGTH_SHORT)
                            .show()*/
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setMainCategoryDate(mainAdapter: MainAdapter) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.mainCategoryRV.layoutManager = LinearLayoutManager(
                this@HomeActivity, LinearLayoutManager.HORIZONTAL, false
            )
            mainAdapter.setOnClickListener(onClickedCategoryItems)
            binding.mainCategoryRV.adapter = mainAdapter

            Log.d("MainActivity", "Get data from room to main rv")
        }
    }

    private fun setSubCategoryDate(subAdapter: SubAdapter) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.rvSubCategory.layoutManager = LinearLayoutManager(
                this@HomeActivity, LinearLayoutManager.VERTICAL, false
            )
            binding.rvSubCategory.adapter = subAdapter
            Log.d("MainActivity", "Get data from room to sub rv")
        }

    }



}