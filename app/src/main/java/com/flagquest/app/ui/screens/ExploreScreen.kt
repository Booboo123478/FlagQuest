package com.flagquest.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.flagquest.app.domain.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onBack: () -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Groupement : continent → sous-région → pays
    val grouped: Map<String, Map<String, List<Country>>> = remember(searchQuery, state.countries) {
        val filtered = if (searchQuery.isBlank()) state.countries
        else state.countries.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.capital.contains(searchQuery, ignoreCase = true)
        }
        filtered
            .sortedBy { it.name }
            .groupBy { it.region }
            .mapValues { (_, countries) -> countries.groupBy { it.subregion } }
            .toSortedMap()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorer les pays") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un pays ou une capitale...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    grouped.forEach { (continent, subregions) ->
                        item(key = continent) {
                            ContinentHeader(
                                continent = continent,
                                countryCount = subregions.values.sumOf { it.size },
                                subregions = subregions
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContinentHeader(
    continent: String,
    countryCount: Int,
    subregions: Map<String, List<Country>>
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = continentEmoji(continent) + "  " + continent,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$countryCount pays",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        AnimatedVisibility(visible = expanded) {
            Column {
                subregions.toSortedMap().forEach { (subregion, countries) ->
                    SubregionSection(subregion = subregion, countries = countries)
                }
            }
        }
    }
}

@Composable
private fun SubregionSection(subregion: String, countries: List<Country>) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(start = 32.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subregion,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${countries.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                countries.forEach { country ->
                    CountryRow(country)
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 48.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryRow(country: Country) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = "${country.name} flag",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(56.dp)
                .height(38.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(country.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                country.capital,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun continentEmoji(region: String): String = when (region) {
    "Europe" -> "🌍"
    "Americas" -> "🌎"
    "Asia" -> "🌏"
    "Africa" -> "🌍"
    "Oceania" -> "🌊"
    "Antarctic" -> "🧊"
    else -> "🌐"
}
