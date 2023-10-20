package com.example.storyapp.utils.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.api.ApiConfig
import com.example.storyapp.database.StoryDatabase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")
object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return UserRepository.getInstance(storyDatabase,pref, apiService)
    }
}