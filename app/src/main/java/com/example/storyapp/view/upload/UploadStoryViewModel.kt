package com.example.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.retrofit.repository.UserRepository
import com.example.storyapp.data.retrofit.response.UploadStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val repository: UserRepository) : ViewModel() {
    val uploadStoryResponse: LiveData<UploadStoryResponse> = repository.uploadResponse
    val isLoading: LiveData<Boolean> = repository.isLoading

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            repository.uploadStory(token, file, description)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

}