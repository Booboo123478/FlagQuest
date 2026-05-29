package com.flagquest.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE region = :region")
    fun getCountriesByRegion(region: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE code = :code LIMIT 1")
    suspend fun getCountryByCode(code: String): CountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun count(): Int
}
