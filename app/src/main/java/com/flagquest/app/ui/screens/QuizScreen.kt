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
import com.flagquest.app.domain.model.QuizConfig
import com.flagquest.app.domain.model.QuizMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    config: QuizConfig,
    onFinished: (Int, Int) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Charge le quiz avec la config au premier lancement
    LaunchedEffect(Unit) { viewModel.loadQuiz(config) }

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
                        Icon(Icons.Default.ArrowBack, "Retour")
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
                    Text("Erreur : ${state.error}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.restart() }) { Text("Réessayer") }
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

                    when (question.mode) {

                        // ── FLAG → NAME ─────────────────────────────────────
                        QuizMode.FLAG_TO_NAME -> {
                            Text(
                                "Quel pays possède ce drapeau ?",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(20.dp))
                            AsyncImage(
                                model = question.country.flagUrl,
                                contentDescription = "Drapeau",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(Modifier.height(28.dp))
                            question.options.forEach { option ->
                                TextAnswerOption(
                                    text = option,
                                    selectedAnswer = state.selectedAnswer,
                                    correctAnswer = question.correctAnswer,
                                    onClick = { viewModel.selectAnswer(option) }
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                        }

                        // ── NAME → FLAG ─────────────────────────────────────
                        QuizMode.NAME_TO_FLAG -> {
                            Text(
                                "Quel est le drapeau de ${question.country.name} ?",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(20.dp))
                            // Grille 2x2 de drapeaux
                            val rows = question.options.chunked(2)
                            rows.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    row.forEach { flagUrl ->
                                        FlagAnswerOption(
                                            flagUrl = flagUrl,
                                            selectedAnswer = state.selectedAnswer,
                                            correctAnswer = question.correctAnswer,
                                            onClick = { viewModel.selectAnswer(flagUrl) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                        }

                        else -> {}
                    }

                    Spacer(Modifier.weight(1f))

                    if (state.selectedAnswer != null) {
                        // Feedback
                        val isCorrect = state.selectedAnswer == question.correctAnswer
                        Text(
                            text = if (isCorrect) "✅ Correct !" else "❌ C'était ${question.country.name}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text(
                                if (state.currentIndex + 1 < state.questions.size) "Suivant" else "Voir les résultats"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextAnswerOption(
    text: String,
    selectedAnswer: String?,
    correctAnswer: String,
    onClick: () -> Unit
) {
    val isAnswered = selectedAnswer != null
    val isCorrect = text == correctAnswer
    val isSelected = selectedAnswer == text

    val containerColor = when {
        isAnswered && isCorrect -> Color(0xFF2E7D32)
        isAnswered && isSelected -> Color(0xFFC62828)
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

@Composable
private fun FlagAnswerOption(
    flagUrl: String,
    selectedAnswer: String?,
    correctAnswer: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnswered = selectedAnswer != null
    val isCorrect = flagUrl == correctAnswer
    val isSelected = selectedAnswer == flagUrl

    val borderColor = when {
        isAnswered && isCorrect -> Color(0xFF4CAF50)
        isAnswered && isSelected -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    val borderWidth = if (isAnswered && (isCorrect || isSelected)) 3.dp else 1.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = !isAnswered) { onClick() }
            .padding(6.dp)
            .height(90.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = flagUrl,
            contentDescription = "Drapeau option",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
