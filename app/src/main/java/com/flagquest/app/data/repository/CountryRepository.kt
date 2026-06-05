package com.flagquest.app.data.repository

import com.flagquest.app.data.local.CountryDao
import com.flagquest.app.data.local.LocalDataSource
import com.flagquest.app.data.local.toDomain
import com.flagquest.app.data.local.toEntity
import com.flagquest.app.data.remote.CountryApiService
import com.flagquest.app.data.remote.toDomain
import com.flagquest.app.domain.model.Country
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    private val api: CountryApiService,
    private val dao: CountryDao,
    private val localDataSource: LocalDataSource
) {
    fun getCountries(): Flow<List<Country>> =
        dao.getAllCountries().map { list -> list.map { it.toDomain() } }

    fun getCountriesByRegion(region: String): Flow<List<Country>> =
        dao.getCountriesByRegion(region).map { list -> list.map { it.toDomain() } }

    private suspend fun loadFromAssets() = withContext(Dispatchers.IO) {
        val countries = localDataSource.loadCountriesFromAssets()
        dao.insertAll(countries.map { it.toDomain().toEntity() })
    }

    suspend fun refreshFromNetwork() = withContext(Dispatchers.IO) {
        try {
            val remote = api.getAllCountries()
            dao.insertAll(remote.map { it.toDomain().toEntity() })
        } catch (e: Exception) {
            // Réseau indisponible, on garde les données locales
        }
    }

    suspend fun ensureLoaded() = withContext(Dispatchers.IO) {
        if (dao.count() == 0) {
            try {
                Log.d("CountryRepo", "Loading from assets...")
                loadFromAssets()
                Log.d("CountryRepo", "Assets loaded: ${dao.count()} countries")
            } catch (e: Exception) {
                Log.e("CountryRepo", "Assets load failed: ${e.message}", e)
                throw Exception("Impossible de charger les pays depuis les assets.", e)
            }
        } else {
            Log.d("CountryRepo", "DB already has ${dao.count()} countries")
        }
        // Tente une mise à jour réseau en arrière-plan (silencieux)
        try { refreshFromNetwork() } catch (_: Exception) {
            Log.d("CountryRepo", "Network refresh skipped (offline)")
        }
    }

    suspend fun getAllCountriesList(): List<Country> = withContext(Dispatchers.IO) {
        dao.getAllCountries().first().map { it.toDomain() }
    }

    suspend fun getRegionsAndSubregions(): Map<String, List<String>> = withContext(Dispatchers.IO) {
        dao.getAllCountries().first()
            .map { it.toDomain() }
            .groupBy { it.region }
            .mapValues { (_, countries) ->
                countries.map { it.subregion }.distinct().sorted()
            }
            .toSortedMap()
    }
}
