package com.yogadimas.storyapp.views.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yogadimas.storyapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun setLogin(
        email: String,
        password: String,
    ) = authRepository.setLogin(email, password).asLiveData()

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

    fun getAuthToken(): LiveData<String?> {
        return authRepository.getAuthToken().asLiveData()
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            authRepository.clearToken()
        }
    }

}