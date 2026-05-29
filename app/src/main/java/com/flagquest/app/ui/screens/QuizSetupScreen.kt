package com.flagquest.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flagquest.app.domain.model.QuizConfig
import com.flagquest.app.domain.model.QuizMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    onStartQuiz: (QuizConfig) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var selectedMode by remember { mutableStateOf(QuizMode.FLAG_TO_NAME) }
    val selectedRegions = remember { mutableStateListOf<String>() }
    val selectedSubregions = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── MODE ──────────────────────────────────────────────
            Text("Game Mode", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            ModeCard(
                selected = selectedMode == QuizMode.FLAG_TO_NAME,
                emoji = "🏳️",
                title = "Flag → Country",
                description = "A flag is shown, guess the country from 4 choices",
                onClick = { selectedMode = QuizMode.FLAG_TO_NAME }
            )
            Spacer(Modifier.height(10.dp))
            ModeCard(
                selected = selectedMode == QuizMode.NAME_TO_FLAG,
                emoji = "🗺️",
                title = "Country → Flag",
                description = "A country is shown, find its flag from 4 choices",
                onClick = { selectedMode = QuizMode.NAME_TO_FLAG }
            )
            Spacer(Modifier.height(10.dp))
            ModeCard(
                selected = selectedMode == QuizMode.MIXED,
                emoji = "🎲",
                title = "Mixed",
                description = "Mix of both modes, for experts",
                onClick = { selectedMode = QuizMode.MIXED }
            )

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            // ── RÉGION ────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Regions", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(8.dp))
                val totalSelected = selectedRegions.size + selectedSubregions.size
                if (totalSelected > 0) {
                    AssistChip(
                        onClick = { selectedRegions.clear(); selectedSubregions.clear() },
                        label = { Text("$totalSelected selected  ✕") }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (selectedRegions.isEmpty() && selectedSubregions.isEmpty())
                    "No selection = Worldwide"
                else
                    "You can combine continents and sub-regions",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                state.regionsMap.forEach { (continent, subregions) ->
                    ContinentSelector(
                        continent = continent,
                        subregions = subregions,
                        isRegionChecked = selectedRegions.contains(continent),
                        checkedSubregions = selectedSubregions.toList(),
                        onToggleRegion = {
                            if (selectedRegions.contains(continent)) {
                                selectedRegions.remove(continent)
                                subregions.forEach { sub ->
                                    selectedSubregions.remove(sub)
                                }
                            } else {
                                selectedRegions.add(continent)
                            }
                        },
                        onToggleSubregion = { sub ->
                            if (selectedSubregions.contains(sub)) {
                                selectedSubregions.remove(sub)
                            } else {
                                selectedSubregions.add(sub)
                            }
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── BOUTON START ──────────────────────────────────────
            val totalSelected = selectedRegions.size + selectedSubregions.size
            Button(
                onClick = {
                    onStartQuiz(
                        QuizConfig(
                            mode = selectedMode,
                            selectedRegions = selectedRegions.toHashSet(),
                            selectedSubregions = selectedSubregions.toHashSet()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                val label = if (totalSelected == 0) "Launch — Worldwide"
                            else "Launch — $totalSelected region(s)"
                Text(label, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun ModeCard(
    selected: Boolean,
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
private fun ContinentSelector(
    continent: String,
    subregions: List<String>,
    isRegionChecked: Boolean,
    checkedSubregions: List<String>,
    onToggleRegion: () -> Unit,
    onToggleSubregion: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val hasSubSelection = subregions.any { checkedSubregions.contains(it) }
    val borderColor = if (isRegionChecked || hasSubSelection)
        MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRegionChecked,
                onCheckedChange = { onToggleRegion() }
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = continent,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown
                    else Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                subregions.forEach { sub ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleSubregion(sub) }
                            .padding(vertical = 2.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedSubregions.contains(sub),
                            onCheckedChange = { onToggleSubregion(sub) }
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(sub, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
