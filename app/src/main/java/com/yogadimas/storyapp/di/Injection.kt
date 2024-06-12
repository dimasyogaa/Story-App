package com.yogadimas.storyapp.di

import android.content.Context
import com.yogadimas.storyapp.data.datastore.AuthPreferences
import com.yogadimas.storyapp.data.datastore.ObjectDatastore.dataStore
import com.yogadimas.storyapp.data.remote.api.ApiConfig
import com.yogadimas.storyapp.data.repository.AuthRepository
import com.yogadimas.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService("")
        val pref = AuthPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = AuthPreferences.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token.orEmpty())
        return StoryRepository.getInstance(apiService)
    }

}