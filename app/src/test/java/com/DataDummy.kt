package com

import androidx.paging.PagingData
import com.example.storyapp.data.retrofit.response.ListStoryItem

object DataDummy {


    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "url_$i",
                createdAt = "created_at_$i",
                name = "author_$i",
                description = "quote_$i",
                lon = 0.0,
                id = "$i",
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}