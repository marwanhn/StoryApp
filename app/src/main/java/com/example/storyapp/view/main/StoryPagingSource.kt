package com.example.storyapp.view.main

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.retrofit.api.ApiService
import com.example.storyapp.data.retrofit.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) :
    PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = userPreference.getSession().first().token

            if (token.isNotEmpty()) {
                val responseData = apiService.getStories(token, position, params.loadSize)
                if (responseData.isSuccessful) {
                    Log.d("Story Paging Source", "Load: ${responseData.body()}")
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    Log.d("Token", "Load Error: $token")
                    LoadResult.Error(Exception("Failed"))
                }
            } else {
                LoadResult.Error(Exception("Failed"))
            }
        } catch (e: Exception) {
            Log.d("Exception", "Load Error: ${e.message}")
            return LoadResult.Error(e)
        }
    }

}