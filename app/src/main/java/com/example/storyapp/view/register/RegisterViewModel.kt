package com.example.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: UserRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = repo.registerResponse
    val isLoading: LiveData<Boolean> = repo.isLoading

    fun register(name: String, email:String, password: String) {
        viewModelScope.launch {
            repo.register(name, email, password)
        }
    }

}