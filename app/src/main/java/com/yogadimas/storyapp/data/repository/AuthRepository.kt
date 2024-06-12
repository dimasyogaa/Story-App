package com.yogadimas.storyapp.data.repository

import com.google.gson.Gson
import com.yogadimas.storyapp.data.datastore.AuthPreferences
import com.yogadimas.storyapp.data.remote.api.ApiService
import com.yogadimas.storyapp.data.remote.model.ErrorResponse
import com.yogadimas.storyapp.data.remote.model.LoginResponse
import com.yogadimas.storyapp.data.remote.model.RegisterResponse
import com.yogadimas.storyapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class AuthRepository private constructor(
    private val apiService: ApiService,
) {
    private lateinit var dataStore: AuthPreferences

    constructor(apiService: ApiService, dataStore: AuthPreferences) : this(apiService) {
        this.dataStore = dataStore
    }

    fun setRegister(
        name: String,
        email: String,
        password: String,
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            emit(Result.Loading())
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (exception: Exception) {
            val e = (exception as? HttpException)?.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(e, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }.flowOn(Dispatchers.IO)

    fun setLogin(
        email: String,
        password: String,
    ): Flow<Result<LoginResponse>> = flow {
        try {
            emit(Result.Loading())
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (exception: Exception) {
            val e = (exception as? HttpException)?.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(e, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token: String) {
        dataStore.saveToken(token)
    }

    fun getAuthToken(): Flow<String?> = dataStore.getToken()

    suspend fun clearToken() {
        dataStore.clearToken()
    }

    companion object {

        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            dataStore: AuthPreferences,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, dataStore)
            }.also { instance = it }

    }


}