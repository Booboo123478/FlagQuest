package com.flagquest.app.domain.usecase

import com.flagquest.app.data.repository.CountryRepository
import com.flagquest.app.domain.model.QuizConfig
import com.flagquest.app.domain.model.QuizMode
import com.flagquest.app.domain.model.QuizQuestion
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val repo: CountryRepository
) {
    suspend operator fun invoke(config: QuizConfig): List<QuizQuestion> {
        repo.ensureLoaded()

        // Pool filtré selon les régions choisies
        val allCountries = repo.getAllCountriesList()
        val pool = when {
            config.isWorldwide -> allCountries
            config.selectedSubregions.isNotEmpty() ->
                allCountries.filter {
                    it.subregion in config.selectedSubregions ||
                    it.region in config.selectedRegions
                }
            else ->
                allCountries.filter { it.region in config.selectedRegions }
        }.shuffled()

        // Fallback si pas assez de pays dans la région
        val questions = if (pool.size >= config.questionCount) {
            pool.take(config.questionCount)
        } else {
            pool
        }

        // Noms pour les mauvaises réponses (toujours depuis le pool global)
        val allNames  = allCountries.map { it.name }
        val allFlags  = allCountries.map { it.flagUrl }

        return questions.map { country ->
            val effectiveMode = if (config.mode == QuizMode.MIXED) {
                if ((0..1).random() == 0) QuizMode.FLAG_TO_NAME else QuizMode.NAME_TO_FLAG
            } else {
                config.mode
            }

            when (effectiveMode) {
                QuizMode.FLAG_TO_NAME -> {
                    // Affiche le drapeau, choisit le nom
                    val wrongOptions = allNames
                        .filter { it != country.name }
                        .shuffled().take(3)
                    QuizQuestion(
                        country = country,
                        options = (wrongOptions + country.name).shuffled(),
                        correctAnswer = country.name,
                        mode = QuizMode.FLAG_TO_NAME
                    )
                }
                QuizMode.NAME_TO_FLAG -> {
                    // Affiche le nom, choisit le drapeau parmi 4
                    val wrongFlags = allCountries
                        .filter { it.code != country.code }
                        .shuffled().take(3)
                        .map { it.flagUrl }
                    QuizQuestion(
                        country = country,
                        options = (wrongFlags + country.flagUrl).shuffled(),
                        correctAnswer = country.flagUrl,
                        mode = QuizMode.NAME_TO_FLAG
                    )
                }
                else -> throw IllegalStateException()
            }
        }
    }
}
