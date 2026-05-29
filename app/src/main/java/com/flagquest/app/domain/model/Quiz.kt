package com.flagquest.app.domain.model

data class QuizQuestion(
    val country: Country,
    val options: List<String>,   // 4 country name choices
    val correctAnswer: String
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeSeconds: Int,
    val score: Int
) {
    val percentage: Float get() = correctAnswers.toFloat() / totalQuestions * 100
}

enum class QuizMode { FLAG_TO_NAME, NAME_TO_FLAG, CAPITAL_TO_FLAG }
enum class Difficulty { EASY, MEDIUM, HARD }
