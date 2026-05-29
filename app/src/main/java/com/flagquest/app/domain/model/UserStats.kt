package com.flagquest.app.domain.model

data class UserStats(
    val userId: String,
    val displayName: String,
    val totalQuizzes: Int = 0,
    val totalCorrect: Int = 0,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0,
    val totalScore: Int = 0,
    val level: Int = 1,
    val xp: Int = 0
) {
    val accuracy: Float get() =
        if (totalQuizzes == 0) 0f else totalCorrect.toFloat() / (totalQuizzes * 10) * 100
}
