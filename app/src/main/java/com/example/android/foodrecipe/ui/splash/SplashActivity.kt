package com.example.android.foodrecipe.ui.splash

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android.foodrecipe.R
import com.example.android.foodrecipe.database.data.RecipesDatabase
import com.example.android.foodrecipe.databinding.ActivitySplashBinding
import com.example.android.foodrecipe.database.entities.main_category.MainCategoryItems
import com.example.android.foodrecipe.database.entities.sub_category.Meal
import com.example.android.foodrecipe.database.entities.sub_category.MealsItems
import com.example.android.foodrecipe.ui.onboarding.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), EasyPermissions.RationaleCallbacks,
    EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }
    private var categoryNameItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        readStorageTask()

    }

    private fun processRender() {
        lifecycleScope.launchWhenStarted {
            viewModel.stateFromAPI.collect {
                when (it) {
                    is SplashViewModel.MainCategoryStateFromAPI.Success -> {
                        insertRecipeDataIntoRoomDB(it.categoryItems)
                        Log.d("SplashActivity", "Itemssss size ${it.categoryItems.categories.size}")
                        Log.d("SplashActivity", "Insert data from api to room data base")
                        for (item in  it.categoryItems.categories) {
                            Log.d("SplashActivity", "Itemsssss ${item.strCategory} ")
                            categoryNameItems.add(item.strCategory)
                        }
                        processMeals()
                    }
                    is SplashViewModel.MainCategoryStateFromAPI.Loading -> {
                        Log.d("SplashActivity","Loading Recipe Data ")
                       /* Toast.makeText(
                            this@SplashActivity,
                            "Loading Recipe Data",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    is SplashViewModel.MainCategoryStateFromAPI.Error -> {
                        Log.d("SplashActivity","Error Recipe Data")
                        /*Toast.makeText(this@SplashActivity, "Error Recipe Data", Toast.LENGTH_SHORT)
                            .show()*/
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun processMeals(){
        lifecycleScope.launch {
            Log.d("SplashActivity","categoryNameItems size ${categoryNameItems.size}")
            for (i in 0 until categoryNameItems.size) {
                viewModel.getMeals(categoryNameItems[i])
                Log.d("SplashActivity","categoryNameItems name ${categoryNameItems[i]}")
            }
                delay(2000L)
                viewModel.stateMeal.collect { filterMeal ->
                    when (filterMeal) {
                        is SplashViewModel.FilterCategoryState.Success -> {
                            insertMealDataIntoRoomDb(filterMeal.categoryName, filterMeal.mealsItems)
                             Log.d("SplashActivity","from processMeals " +
                                     "${filterMeal.mealsItems.meals?.get(0)?.categoryName} " +
                                     filterMeal.categoryName
                             )
                        }
                        is SplashViewModel.FilterCategoryState.Loading -> {
                            Log.d("SplashActivity","Loading Meals Data")
                           /* Toast.makeText(
                                this@SplashActivity,
                                "Loading Meals Data",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                        is SplashViewModel.FilterCategoryState.Error -> {
                            Log.d("SplashActivity","Error Meals Data")
                           /* Toast.makeText(
                                this@SplashActivity,
                                "Error Meals Data",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                        else -> Unit
                    }
                }
        }
    }


    private fun insertRecipeDataIntoRoomDB(categoryItems: MainCategoryItems) {
        lifecycleScope.launch(Dispatchers.Default) {
            this.let {
                clearDatabase()
                for (element in categoryItems.categories) {
                    RecipesDatabase.getDatabase(this@SplashActivity)
                        .recipeDao().insertCategory(element)
                }
            }
        }
    }

    private fun clearDatabase() {
        lifecycleScope.launch(Dispatchers.Default) {
            this.let {
                RecipesDatabase.getDatabase(this@SplashActivity).recipeDao().clearCategoryDatabase()
                RecipesDatabase.getDatabase(this@SplashActivity).recipeDao().clearMealDatabase()
            }
        }
    }

    private fun insertMealDataIntoRoomDb(categoryName: String, meal: MealsItems?) {
        lifecycleScope.launch(Dispatchers.Default) {
            this.let {
                for (arr in meal!!.meals!!) {
                    val mealItemModel = Meal(
                        id = arr.id,
                        idMeal = arr.idMeal,
                        categoryName = categoryName,
                        strMeal = arr.strMeal,
                        strMealThumb = arr.strMealThumb
                    )
                    RecipesDatabase.getDatabase(this@SplashActivity)
                        .recipeDao().insertMeal(mealItemModel)
                   // Log.d("SplashActivity", arr.toString())
                }
            }
        }


    }

    private fun hasReadStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun readStorageTask() {
        if (hasReadStoragePermission()) {
            processRender()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@SplashActivity,
                    Pair.create(binding.splashCard, "splash")
                )
                startActivity(intent, options.toBundle())
            }, 3000)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage,",
                123,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)


    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d("SplashActivity","onRationaleDenied 1 + $requestCode")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d("SplashActivity","onRationaleAccepted + $requestCode")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
       /*if( EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
            Log.d("SplashActivity","onPermissionsDenied 2 + $requestCode + $perms")
        }*/
        AppSettingsDialog.Builder(this).build().show()
        Log.d("SplashActivity","onPermissionsDenied 1 + $requestCode")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        processRender()
        Log.d("SplashActivity","onPermissionsGranted + $requestCode")
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this@SplashActivity,
                Pair.create(binding.splashCard, "splash")
            )
            startActivity(intent, options.toBundle())
        }, 1000)
    }
}