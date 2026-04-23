package com.example.llmlearningassistant.data

/**
 * A learning task/quiz - shown on Home screen and opened on the task detail screen.
 * Each task has a title, description, topic, and a list of questions.
 */
data class LearningTask(
    val id: Int,
    val title: String,
    val description: String,
    val topic: String,
    val questions: List<Question>
)
