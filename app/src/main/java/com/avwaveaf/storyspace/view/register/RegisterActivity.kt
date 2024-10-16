package com.avwaveaf.storyspace.view.register

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
import com.avwaveaf.storyspace.databinding.ActivityRegisterBinding
import com.avwaveaf.storyspace.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        setupListeners()
        setupTextWatchers()
        observeViewModel()

    }

    private fun setupTextWatchers() {
        val emailEditText = binding.edRegisterEmail
        val passwordEditText = binding.edRegisterPassword

        emailEditText.addTextChangedListener {
            validateInput()
        }

        passwordEditText.addTextChangedListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val emailIsValid = binding.edRegisterEmail.isEmailValid()
        val passwordIsValid = binding.edRegisterPassword.isPasswordValid()
        val nameIsNotEmpty = binding.edRegisterName.text.isNullOrBlank()

        // Enable button if both fields are valid
        binding.btnRegister.isEnabled =
            emailIsValid && passwordIsValid && binding.edRegisterEmail.text.toString()
                .isNotEmpty() && binding.edRegisterPassword.text.toString()
                .isNotEmpty() && !nameIsNotEmpty
    }

    private fun playAnimation() {
        val logoTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(250)
        val logoSubtitle =
            ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(250)
        val loginTitle =
            ObjectAnimator.ofFloat(binding.tvRegisterHeadline, View.ALPHA, 1f).setDuration(250)
        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(250)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(250)
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
                name,
                email,
                password,
                loginBtn,
                registerHeadline,
                registerButton
            )
            start()
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

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            navigateToLoginScreen()
        }

        binding.btnRegister.setOnClickListener {
            showLoading(true)
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            // Validate inputs (you can enhance this later)
            if (name.isNotBlank() && email.isNotBlank() && password.length >= 8) {
                registerUser(name, email, password)
            } else {
                showLoading(false)
                Snackbar.make(
                    binding.root,
                    getString(R.string.register_detail_invalid), Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModel.register(name, email, password)
    }

    private fun observeViewModel() {
        viewModel.registrationResult.observe(this) { result ->
            showLoading(false)
            Snackbar.make(binding.root, result, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.registrationResultError.observe(this) { error ->
            if (!error) {
                navigateToLoginScreen()
            }
        }
    }


    private fun navigateToLoginScreen() {
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