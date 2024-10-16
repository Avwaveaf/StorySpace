package com.avwaveaf.storyspace.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.widget.addTextChangedListener
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.databinding.ActivityLoginBinding
import com.avwaveaf.storyspace.view.home.HomeActivity
import com.avwaveaf.storyspace.view.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        // Initially disable the button
        binding.btnLogin.isEnabled = false

        // Set up text watchers
        setupTextWatchers()

       binding.btnRegister.setOnClickListener {
           val intent = Intent(this, RegisterActivity::class.java)

           startActivity(
               intent,
               ActivityOptionsCompat.makeSceneTransitionAnimation(
                   this,
                   Pair.create(binding.view2, "sharedCircle"),
                   Pair.create(binding.imageView, "sharedLogo")
               ).toBundle()
           )
       }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            showLoading(true)

            loginViewModel.login(email, password) { result ->
                result.onSuccess {
//                     Navigate to HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    showLoading(false)
                    startActivity(intent)
                    finish()
                }.onFailure { exception ->
                    showLoading(false)

                    // Show error message using Snackbar
                    Snackbar.make(binding.root, exception.message ?: getString(R.string.login_failed), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(flag: Boolean) {
        if (flag) {
            with(binding) {
                pbLoading.visibility = View.VISIBLE
                laodingOverlay.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                pbLoading.visibility = View.GONE
                laodingOverlay.visibility = View.GONE
            }
        }
    }

    private fun playAnimation() {
        val logoTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(250)
        val logoSubtitle =
            ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(250)
        val loginTitle =
            ObjectAnimator.ofFloat(binding.tvLoginHeadline, View.ALPHA, 1f).setDuration(250)
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(250)
        val loginBtn =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)
        val registerHeadline =
            ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(250)
        val registerButton =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                logoTitle,
                logoSubtitle,
                loginTitle,
                email,
                password,
                loginBtn,
                registerHeadline,
                registerButton
            )
            start()
        }
    }

    private fun setupTextWatchers() {
        val emailEditText = binding.edLoginEmail
        val passwordEditText = binding.edLoginPassword

        emailEditText.addTextChangedListener {
            validateInput()
        }

        passwordEditText.addTextChangedListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val emailIsValid = binding.edLoginEmail.isEmailValid()
        val passwordIsValid = binding.edLoginPassword.isPasswordValid()

        // Enable button if both fields are valid
        binding.btnLogin.isEnabled =
            emailIsValid && passwordIsValid && binding.edLoginEmail.text.toString()
                .isNotEmpty() && binding.edLoginPassword.text.toString().isNotEmpty()
    }
}