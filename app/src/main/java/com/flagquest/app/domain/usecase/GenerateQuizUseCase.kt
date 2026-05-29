package com.flagquest.app.domain.usecase

import com.flagquest.app.data.repository.CountryRepository
import com.flagquest.app.domain.model.QuizQuestion
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val repo: CountryRepository
) {
    suspend operator fun invoke(questionCount: Int = 10): List<QuizQuestion> {
        repo.ensureLoaded()
        val pool = repo.getRandomCountries(questionCount + 30)
        val questions = pool.take(questionCount)
        val allNames = pool.map { it.name }

        return questions.map { country ->
            val wrongOptions = allNames
                .filter { it != country.name }
                .shuffled()
                .take(3)
            val options = (wrongOptions + country.name).shuffled()
            QuizQuestion(
                country = country,
                options = options,
                correctAnswer = country.name
            )
        }
    }
}
