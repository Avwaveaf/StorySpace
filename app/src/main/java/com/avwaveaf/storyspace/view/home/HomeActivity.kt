package com.avwaveaf.storyspace.view.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
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
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val menuItem = menu?.findItem(R.id.action_logout)
        menuItem?.actionView = layoutInflater.inflate(R.layout.menu_item_logout, null)

        // Set click listener on the custom view
        menuItem?.actionView?.setOnClickListener {
            logout()
        }

        return true
    }


    private fun logout() {
        // Assuming you have access to SessionManager
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.clearSession() // Clear session
            // Redirect to LoginActivity
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish() // Finish HomeActivity
        }
    }
}