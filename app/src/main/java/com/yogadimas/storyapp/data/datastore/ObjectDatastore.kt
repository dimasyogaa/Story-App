package com.yogadimas.storyapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object ObjectDatastore {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

}