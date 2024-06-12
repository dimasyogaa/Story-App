package com.yogadimas.storyapp.views.activity.intrfaces

import com.yogadimas.storyapp.data.remote.model.ListStoryItem

interface OnItemClickCallback {
    fun onItemClicked(data: ListStoryItem)
}
