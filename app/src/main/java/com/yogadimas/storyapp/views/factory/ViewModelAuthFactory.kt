package com.yogadimas.storyapp.views.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogadimas.storyapp.data.repository.AuthRepository
import com.yogadimas.storyapp.di.Injection
import com.yogadimas.storyapp.views.activity.login.LoginViewModel
import com.yogadimas.storyapp.views.activity.signup.SignupViewModel


class ViewModelAuthFactory private constructor(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelAuthFactory? = null
        fun getInstance(
            context: Context,
        ): ViewModelAuthFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelAuthFactory(Injection.provideRepository(context))
            }.also { instance = it }

    }

}

