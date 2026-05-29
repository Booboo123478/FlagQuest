package com.flagquest.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagquest.app.domain.model.QuizQuestion
import com.flagquest.app.domain.usecase.GenerateQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
    val error: String? = null
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val progress: Float get() = if (questions.isEmpty()) 0f else currentIndex.toFloat() / questions.size
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val generateQuiz: GenerateQuizUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    init { loadQuiz() }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                val questions = generateQuiz()
                _state.value = QuizUiState(isLoading = false, questions = questions)
            } catch (e: Exception) {
                _state.value = QuizUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun selectAnswer(answer: String) {
        val current = _state.value
        if (current.selectedAnswer != null) return
        val correct = current.currentQuestion?.correctAnswer == answer
        _state.value = current.copy(
            selectedAnswer = answer,
            correctCount = if (correct) current.correctCount + 1 else current.correctCount
        )
    }

    fun nextQuestion() {
        val current = _state.value
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            _state.value = current.copy(isFinished = true)
        } else {
            _state.value = current.copy(currentIndex = nextIndex, selectedAnswer = null)
        }
    }

    fun restart() { loadQuiz() }
}
