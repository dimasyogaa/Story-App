package com.yogadimas.storyapp.views.activity.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.databinding.ActivitySignupBinding
import com.yogadimas.storyapp.utils.Result
import com.yogadimas.storyapp.utils.isValidString
import com.yogadimas.storyapp.views.activity.login.LoginActivity
import com.yogadimas.storyapp.views.factory.ViewModelAuthFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val viewModel: SignupViewModel by viewModels {
        ViewModelAuthFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun checkPassword() {
        binding.passwordEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.passwordEditTextLayout.apply {

                        if (s.toString().length < 8) {
                            isErrorEnabled = true
                            error = context.getString(R.string.error_password)
                        } else {
                            error = null
                            isErrorEnabled = false
                        }

                    }


                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val btnSignup =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val edtName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val edtEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                edtName,
                email,
                edtEmail,
                password,
                edtPassword,
                btnSignup
            )
            startDelay = 500
        }.start()
    }

    private fun setupAction() {
        val getEmail = obtainIntent(EXTRA_EMAIL)
        val getPassword = obtainIntent(EXTRA_PASSWORD)

        binding.apply {
            emailEditText.setText(getEmail)
            passwordEditText.setText(getPassword)
        }

        checkEmail()
        checkPassword()

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()


            if (allInputAreFilled(name, email) && validationEmail()) {
                viewModel.setRegister(name, email, password).observe(this) {

                    when (it) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            val result = it.data
                            if (result?.error != true) {
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.titlle_dialog_success))
                                    setMessage(getString(R.string.register_successs))
                                    setPositiveButton(getString(R.string.text_continue)) { _, _ ->
                                        startActivity(
                                            Intent(
                                                this@SignupActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }

                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this@SignupActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }


        }

    }

    private fun allInputAreFilled(
        name: String,
        email: String,
    ): Boolean {
        var allInputAreFilled = false

        binding.apply {
            if (name.isEmpty()) {
                checkInputIsEmpty(
                    nameEditTextLayout,
                    getString(R.string.name)
                )
            } else {
                checkInputIsEmpty(nameEditTextLayout)
                if (email.isEmpty()) {
                    checkInputIsEmpty(
                        emailEditTextLayout,
                        getString(R.string.email)
                    )
                } else {
                    checkInputIsEmpty(emailEditTextLayout)
                    allInputAreFilled = true
                }
            }
        }

        return allInputAreFilled
    }

    private fun checkInputIsEmpty(
        inputView: TextInputLayout,
        message: String? = null,
    ) {
        if (message != null) {
            inputView.isErrorEnabled = true
            inputView.error = stringFormat(message)
        } else {
            inputView.error = null
            inputView.isErrorEnabled = false
        }

    }

    private fun checkEmail() {
        binding.emailEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validationEmail()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }
    }

    private fun validationEmail(): Boolean {
        var isValid = false

        binding.emailEditTextLayout.apply {
            if (!isValidString(binding.emailEditText.text.toString())) {
                isErrorEnabled = true
                error = context.getString(R.string.error_email)
            } else {
                error = null
                isErrorEnabled = false
                isValid = true
            }
        }
        return isValid
    }

    private fun obtainIntent(key: String): String? {
        return intent.getStringExtra(key)
    }

    private fun stringFormat(string: String): String {
        return String.format(getString(R.string.empty_field), string)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
    }

}