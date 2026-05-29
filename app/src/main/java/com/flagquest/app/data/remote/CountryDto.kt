package com.flagquest.app.data.remote

import com.google.gson.annotations.SerializedName
import com.flagquest.app.domain.model.Country

data class CountryDto(
    @SerializedName("cca2") val code: String,
    @SerializedName("name") val name: NameDto,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("region") val region: String,
    @SerializedName("subregion") val subregion: String?,
    @SerializedName("flags") val flags: FlagsDto,
    @SerializedName("population") val population: Long,
    @SerializedName("area") val area: Double?,
    @SerializedName("languages") val languages: Map<String, String>?,
    @SerializedName("currencies") val currencies: Map<String, CurrencyDto>?
)

data class NameDto(
    @SerializedName("common") val common: String,
    @SerializedName("official") val official: String
)

data class FlagsDto(
    @SerializedName("png") val png: String,
    @SerializedName("svg") val svg: String
)

data class CurrencyDto(
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String?
)

fun CountryDto.toDomain(): Country = Country(
    code = code,
    name = name.common,
    capital = capital?.firstOrNull() ?: "N/A",
    region = region,
    subregion = subregion ?: region,
    flagUrl = flags.png,
    population = population,
    area = area ?: 0.0,
    languages = languages?.values?.toList() ?: emptyList(),
    currencies = currencies?.values?.map { it.name } ?: emptyList()
)
