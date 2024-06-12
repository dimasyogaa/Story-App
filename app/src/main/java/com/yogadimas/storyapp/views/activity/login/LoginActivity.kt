package com.yogadimas.storyapp.views.activity.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.databinding.ActivityLoginBinding
import com.yogadimas.storyapp.utils.Result
import com.yogadimas.storyapp.views.activity.main.MainActivity
import com.yogadimas.storyapp.views.activity.signup.SignupActivity
import com.yogadimas.storyapp.views.factory.ViewModelAuthFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelAuthFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLogin()

        setupView()

        playAnimation()

        setupAction()

    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val edtEmail =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, edtEmail, edtPassword, btnLogin)
            startDelay = 500
        }.start()
    }

    private fun checkLogin() {
        viewModel.getAuthToken().observe(this) {
            if (it != null) {
                AlertDialog.Builder(this).apply {
                    setCancelable(false)
                    setTitle(getString(R.string.titlle_dialog_success))
                    setMessage(getString(R.string.login_success))
                    setPositiveButton(getString(R.string.text_continue)) { _, _ ->
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.setLogin(email, password).observe(this) {

                when (it) {

                    is Result.Loading -> showLoading(true)

                    is Result.Success -> {
                        showLoading(false)
                        val result = it.data
                        if (result?.error != true && result?.loginResult != null) {
                            val token = result.loginResult.token
                            if (token != null) {
                                viewModel.saveAuthToken(token)
                            }
                        }

                    }

                    is Result.Error -> {
                        showLoading(false)
                        val message = it.message
                        if (message.equals(getString(R.string.user_not_found))) {
                            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                            intent.putExtra(SignupActivity.EXTRA_EMAIL, email)
                            intent.putExtra(SignupActivity.EXTRA_PASSWORD, password)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@LoginActivity, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

            }


        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}