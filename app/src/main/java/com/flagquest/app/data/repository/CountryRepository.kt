package com.flagquest.app.data.repository

import com.flagquest.app.data.local.CountryDao
import com.flagquest.app.data.local.toDomain
import com.flagquest.app.data.local.toEntity
import com.flagquest.app.data.remote.CountryApiService
import com.flagquest.app.data.remote.toDomain
import com.flagquest.app.domain.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun refreshCountries() {
        val remote = api.getAllCountries()
        dao.insertAll(remote.map { it.toDomain().toEntity() })
    }

    suspend fun ensureLoaded() {
        if (dao.count() == 0) refreshCountries()
    }

    suspend fun getRandomCountries(n: Int): List<Country> {
        // Pull all from DB, shuffle, take n
        var result = emptyList<Country>()
        dao.getAllCountries().collect { list ->
            result = list.map { it.toDomain() }.shuffled().take(n)
        }
        return result
    }
}
