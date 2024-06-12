package com.yogadimas.storyapp.views.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yogadimas.storyapp.data.remote.model.StoryResponse
import com.yogadimas.storyapp.data.repository.StoryRepository
import com.yogadimas.storyapp.utils.Result

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(): LiveData<Result<StoryResponse>> = storyRepository.getStories().asLiveData()

}