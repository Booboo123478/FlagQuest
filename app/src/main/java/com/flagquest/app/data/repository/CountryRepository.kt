package com.flagquest.app.data.repository

import com.flagquest.app.data.local.CountryDao
import com.flagquest.app.data.local.toDomain
import com.flagquest.app.data.local.toEntity
import com.flagquest.app.data.remote.CountryApiService
import com.flagquest.app.data.remote.toDomain
import com.flagquest.app.domain.model.Country
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
    private val dao: CountryDao
) {
    fun getCountries(): Flow<List<Country>> =
        dao.getAllCountries().map { list -> list.map { it.toDomain() } }

    fun getCountriesByRegion(region: String): Flow<List<Country>> =
        dao.getCountriesByRegion(region).map { list -> list.map { it.toDomain() } }

    suspend fun refreshCountries() = withContext(Dispatchers.IO) {
        val remote = api.getAllCountries()
        dao.insertAll(remote.map { it.toDomain().toEntity() })
    }

    suspend fun ensureLoaded() = withContext(Dispatchers.IO) {
        if (dao.count() == 0) refreshCountries()
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
