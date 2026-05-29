package com.flagquest.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val percentage = (score.toFloat() / total * 100).toInt()
    val emoji = when {
        percentage >= 90 -> "🏆"
        percentage >= 70 -> "🎉"
        percentage >= 50 -> "👍"
        else -> "📚"
    }
    val message = when {
        percentage >= 90 -> "Outstanding!"
        percentage >= 70 -> "Great job!"
        percentage >= 50 -> "Good effort!"
        else -> "Keep practicing!"
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "$score / $total",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$percentage% correct",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Replay, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Play Again")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Home")
            }
        }
    }
}
