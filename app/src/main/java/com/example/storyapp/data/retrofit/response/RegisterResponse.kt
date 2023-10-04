package com.example.storyapp.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
    var error: Boolean,

	@field:SerializedName("message")
	val message: String
)
