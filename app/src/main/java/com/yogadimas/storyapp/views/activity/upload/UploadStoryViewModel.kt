package com.yogadimas.storyapp.views.activity.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yogadimas.storyapp.data.remote.model.ErrorResponse
import com.yogadimas.storyapp.data.repository.StoryRepository
import com.yogadimas.storyapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun setStory(
        description: RequestBody,
        file: MultipartBody.Part,
    ): LiveData<Result<ErrorResponse>> = storyRepository.setStory(description, file).asLiveData()

}