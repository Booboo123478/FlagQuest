package com.flagquest.app.data.local

import android.content.Context
import com.flagquest.app.data.remote.CountryDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadCountriesFromAssets(): List<CountryDto> {
        val json = context.assets.open("countries.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<CountryDto>>() {}.type
        return Gson().fromJson(json, type)
    }
}
