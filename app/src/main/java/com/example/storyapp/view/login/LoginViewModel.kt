package com.example.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.response.LoginResponse
import com.example.storyapp.utils.EventHandler
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    val loginResponse: LiveData<LoginResponse> = repository.loginResponse
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<EventHandler<String>> = repository.toastText

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getLogin(email: String, password: String) {
        viewModelScope.launch {
            repository.getLogin(email,password)
        }
    }

    fun login() {
        viewModelScope.launch {
            repository.login()
        }
    }



}