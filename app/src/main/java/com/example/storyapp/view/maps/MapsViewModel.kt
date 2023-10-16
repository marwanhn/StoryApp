package com.example.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.response.StoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    val markers: LiveData<StoryResponse> = repository.listResponse
    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(token)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}