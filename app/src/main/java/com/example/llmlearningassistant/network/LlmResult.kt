package com.example.llmlearningassistant.network

/**
 * Simple Result wrapper used by the Gemini client so the UI can render
 * loading / success / failure states as required by the task brief.
 */
sealed class LlmResult<out T> {
    object Loading : LlmResult<Nothing>()
    data class Success<T>(val data: T) : LlmResult<T>()
    data class Error(val message: String) : LlmResult<Nothing>()
}
