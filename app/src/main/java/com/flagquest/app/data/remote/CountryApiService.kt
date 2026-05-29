package com.flagquest.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApiService {

    @GET("v3.1/all?fields=cca2,name,capital,region,subregion,flags,population,area,languages,currencies")
    suspend fun getAllCountries(): List<CountryDto>

    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): List<CountryDto>
}
