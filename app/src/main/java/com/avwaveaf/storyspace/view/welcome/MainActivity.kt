package com.avwaveaf.storyspace.view.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.databinding.ActivityMainBinding
import com.avwaveaf.storyspace.utils.SessionManager
import com.avwaveaf.storyspace.view.home.HomeActivity
import com.avwaveaf.storyspace.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Check if user is already logged in
        CoroutineScope(Dispatchers.Main).launch {
            sessionManager.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    // Navigate to main screen if user is logged in
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Finish MainActivity to prevent returning to it
                } else {
                    // Proceed with the welcome screen setup if user is not logged in
                    setupWelcomeScreen()
                }
            }
        }

    }

    private fun setupWelcomeScreen() {
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up button click listener to navigate to LoginActivity
        binding.btnStart.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair.create(binding.view2, "sharedCircle"),
                    Pair.create(binding.imageView, "sharedLogo")
                ).toBundle()
            )

            finish()
        }
    }
}