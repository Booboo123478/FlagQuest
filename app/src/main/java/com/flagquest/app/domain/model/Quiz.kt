package com.flagquest.app.domain.model

data class QuizQuestion(
    val country: Country,
    val options: List<String>,   // 4 choices (names or flag URLs depending on mode)
    val correctAnswer: String,
    val mode: QuizMode
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeSeconds: Int,
    val score: Int
) {
    val percentage: Float get() = correctAnswers.toFloat() / totalQuestions * 100
}

enum class QuizMode { FLAG_TO_NAME, NAME_TO_FLAG, MIXED }

data class QuizConfig(
    val mode: QuizMode = QuizMode.FLAG_TO_NAME,
    val selectedRegions: Set<String> = emptySet(),     // continents sélectionnés
    val selectedSubregions: Set<String> = emptySet(),  // sous-régions sélectionnées
    val questionCount: Int = 10
) {
    // Si rien de sélectionné = monde entier
    val isWorldwide: Boolean get() = selectedRegions.isEmpty() && selectedSubregions.isEmpty()
}
