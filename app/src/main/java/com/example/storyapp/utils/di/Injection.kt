package com.example.storyapp.utils.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")
object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUserToken().first() }
        return UserRepository.getInstance(pref, apiService)
    }
}