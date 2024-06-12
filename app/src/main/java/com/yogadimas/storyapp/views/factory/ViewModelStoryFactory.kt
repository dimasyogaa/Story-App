package com.yogadimas.storyapp.views.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogadimas.storyapp.data.repository.StoryRepository
import com.yogadimas.storyapp.di.Injection
import com.yogadimas.storyapp.views.activity.main.MainViewModel
import com.yogadimas.storyapp.views.activity.upload.UploadStoryViewModel

class ViewModelStoryFactory private constructor(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelStoryFactory? = null
        fun getInstance(
            context: Context,
        ): ViewModelStoryFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelStoryFactory(Injection.provideStoryRepository(context))
            }.also { instance = it }

    }
}