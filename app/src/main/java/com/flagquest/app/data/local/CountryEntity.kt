package com.flagquest.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flagquest.app.domain.model.Country

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val flagUrl: String,
    val population: Long,
    val area: Double,
    val languages: String,    // JSON-serialized list
    val currencies: String    // JSON-serialized list
)

fun CountryEntity.toDomain(): Country = Country(
    code = code,
    name = name,
    capital = capital,
    region = region,
    flagUrl = flagUrl,
    population = population,
    area = area,
    languages = languages.split("|"),
    currencies = currencies.split("|")
)

fun Country.toEntity(): CountryEntity = CountryEntity(
    code = code,
    name = name,
    capital = capital,
    region = region,
    flagUrl = flagUrl,
    population = population,
    area = area,
    languages = languages.joinToString("|"),
    currencies = currencies.joinToString("|")
)
