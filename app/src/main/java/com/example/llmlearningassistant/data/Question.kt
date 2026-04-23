package com.example.llmlearningassistant.data

/**
 * A multiple-choice question inside a learning Task.
 * `correctIndex` is the index into [options] that is the correct answer.
 */
data class Question(
    val id: Int,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val topic: String
)
