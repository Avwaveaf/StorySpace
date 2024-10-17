package com.avwaveaf.storyspace.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.databinding.ActivityHomeBinding
import com.avwaveaf.storyspace.utils.SessionManager
import com.avwaveaf.storyspace.view.welcome.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.fabCompose.setOnClickListener {
            navController.navigate(R.id.action_global_composeStoryFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @SuppressLint("InflateParams")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val menuItem = menu?.findItem(R.id.action_logout)
        menuItem?.actionView = layoutInflater.inflate(R.layout.menu_item_logout, null)

        menuItem?.actionView?.setOnClickListener {
            logout()
        }

        return true
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.clearSession()
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}