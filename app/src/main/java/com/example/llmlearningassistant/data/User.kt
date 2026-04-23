package com.example.llmlearningassistant.data

/**
 * Simple user data model held locally (SharedPreferences).
 * No backend required - task brief permits dummy/local data.
 */
data class User(
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val interests: List<String> = emptyList()
)
