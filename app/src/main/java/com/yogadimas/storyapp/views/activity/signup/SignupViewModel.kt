package com.yogadimas.storyapp.views.activity.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yogadimas.storyapp.data.repository.AuthRepository

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun setRegister(
        name: String,
        email: String,
        password: String,
    ) = authRepository.setRegister(name, email, password).asLiveData()

}