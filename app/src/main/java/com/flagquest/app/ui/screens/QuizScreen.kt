package com.flagquest.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onFinished: (Int, Int) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished(state.correctCount, state.questions.size)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${state.error}")
                    Button(onClick = { viewModel.restart() }) { Text("Retry") }
                }
            }

            state.currentQuestion != null -> {
                val question = state.currentQuestion!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Question ${state.currentIndex + 1} / ${state.questions.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Which country does this flag belong to?",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(24.dp))

                    AsyncImage(
                        model = question.country.flagUrl,
                        contentDescription = "Flag",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(Modifier.height(32.dp))

                    question.options.forEach { option ->
                        AnswerOption(
                            text = option,
                            selectedAnswer = state.selectedAnswer,
                            correctAnswer = question.correctAnswer,
                            onClick = { viewModel.selectAnswer(option) }
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    Spacer(Modifier.weight(1f))

                    if (state.selectedAnswer != null) {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text(
                                if (state.currentIndex + 1 < state.questions.size) "Next" else "See Results"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    selectedAnswer: String?,
    correctAnswer: String,
    onClick: () -> Unit
) {
    val isSelected = selectedAnswer == text
    val isAnswered = selectedAnswer != null
    val isCorrect = text == correctAnswer

    val containerColor = when {
        isAnswered && isCorrect -> Color(0xFF2E7D32)
        isAnswered && isSelected && !isCorrect -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isAnswered && isCorrect -> Color(0xFF4CAF50)
        isAnswered && isSelected -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isAnswered) { onClick() }
            .padding(16.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
