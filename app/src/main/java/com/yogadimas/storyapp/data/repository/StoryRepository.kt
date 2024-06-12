package com.yogadimas.storyapp.data.repository

import com.google.gson.Gson
import com.yogadimas.storyapp.data.remote.api.ApiService
import com.yogadimas.storyapp.data.remote.model.ErrorResponse
import com.yogadimas.storyapp.data.remote.model.StoryResponse
import com.yogadimas.storyapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
) {

    fun getStories(): Flow<Result<StoryResponse>> = flow {
        try {
            emit(Result.Loading())
            val response = apiService.getStories()
            emit(Result.Success(response))
        } catch (exception: Exception) {
            val e = (exception as? HttpException)?.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(e, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: "null"
            emit(Result.Error(errorMessage))
        }
    }.flowOn(Dispatchers.IO)

    fun setStory(
        description: RequestBody,
        file: MultipartBody.Part,
    ): Flow<Result<ErrorResponse>> = flow {
        try {
            emit(Result.Loading())
            val response = apiService.setStory(description, file)
            emit(Result.Success(response))
        } catch (exception: Exception) {
            val e = (exception as? HttpException)?.response()?.errorBody()?.string()
            emit(Result.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }

    }

}