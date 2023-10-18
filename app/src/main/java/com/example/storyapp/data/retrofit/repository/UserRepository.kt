package com.example.storyapp.data.retrofit.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.retrofit.api.ApiService
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.data.retrofit.response.LoginResponse
import com.example.storyapp.data.retrofit.response.RegisterResponse
import com.example.storyapp.data.retrofit.response.StoryResponse
import com.example.storyapp.data.retrofit.response.UploadStoryResponse
import com.example.storyapp.utils.EventHandler
import com.example.storyapp.view.main.StoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService
) {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _toastText = MutableLiveData<EventHandler<String>>()
    val toastText: LiveData<EventHandler<String>> = _toastText

    private val _listResponse = MutableLiveData<StoryResponse>()
    val listResponse: LiveData<StoryResponse> = _listResponse

    private val _customEvent = MutableLiveData<String>()
    val customEvent: LiveData<String> = _customEvent

    private val _uploadResponse = MutableLiveData<UploadStoryResponse>()
    val uploadResponse: LiveData<UploadStoryResponse> = _uploadResponse


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _registerResponse.value = response.body()
                } else {
                    _customEvent.value = "Terjadi kesalahan saat registrasi"
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _customEvent.value = "Terjadi kesalahan saat registrasi"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getLogin(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.login(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>, response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                } else {
                    _toastText.value = EventHandler(response.body()?.message.toString())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = EventHandler(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getListStory(): LiveData<PagingData<ListStoryItem>> {
        _isLoading.value = true
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
        val client = apiService.getStories()

        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _listResponse.value = response.body()
                } else {

                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getStoriesWithLocation(token: String) {
        _isLoading.value = true
        val client = apiService.getStoriesWithLocation(token)

        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _listResponse.value = response.body()
                } else {

                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStory(token: String, photo: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = apiService.uploadImage(token, photo, description)

        client.enqueue(object : Callback<UploadStoryResponse> {
            override fun onResponse(
                call: Call<UploadStoryResponse>, response: Response<UploadStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _uploadResponse.value = response.body()
                    _toastText.value = EventHandler(response.body()?.message.toString())
                } else {
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                Log.d("error upload", t.message.toString())
            }

        })
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun login() {
        userPreference.login()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        private const val TAG = "UserRepository"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userPreference, apiService)
        }.also { instance = it }
    }
}