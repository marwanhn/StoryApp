package com.example.storyapp.utils

import java.util.concurrent.atomic.AtomicBoolean

class EventHandler<out T>(private val content: T) {

    private val hasBeenHandled = AtomicBoolean(false)

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled.getAndSet(true)) {
            null
        } else {
            content
        }
    }
}
