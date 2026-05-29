package com.flagquest.app.domain.model

data class Country(
    val code: String,        // ISO 3166-1 alpha-2  e.g. "FR"
    val name: String,        // "France"
    val capital: String,
    val region: String,
    val flagUrl: String,     // SVG/PNG URL from restcountries.com
    val population: Long,
    val area: Double,
    val languages: List<String>,
    val currencies: List<String>
)
