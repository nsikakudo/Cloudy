package com.project.cloudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.cloudy.databinding.ActivityMainBinding
import com.project.cloudy.ui.HomeFragment
import com.project.cloudy.ui.SearchLocationFragment
import com.project.cloudy.ui.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.InternalChannelz.id

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        val fragHost = supportFragmentManager.findFragmentById(R.id.fragHost) as NavHostFragment
//        navController = fragHost.findNavController()
//
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.homeFragment,
//                R.id.searchLocationFragment,
//                R.id.settingsFragment
//            )
//        )
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        binding.bottomNavigationView.setupWithNavController(navController)
//
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            binding.bottomNavigationView.isVisible =
//                appBarConfiguration.topLevelDestinations.contains(destination.id)
//        }


//        replaceFragment(HomeFragment())
//
//        binding.bottomNavigationView.setOnItemSelectedListener {
//            when(it.itemId){
//
//                R.id.home -> replaceFragment(HomeFragment())
//                R.id.search -> replaceFragment(SearchLocationFragment())
//                R.id.settings -> replaceFragment(SettingsFragment())
//
//                else ->{
//                }
//            }
//            true
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
//    private fun replaceFragment(fragment: Fragment){
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.fragHost, fragment)
//        fragmentTransaction.commit()
//    }
}