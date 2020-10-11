package com.example.countries.network

import com.example.countries.landingscreen.model.country.CountryDetailsResp
import com.example.countries.landingscreen.model.weather.WeatherResp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("all")
    fun getCountryList(): Call<ArrayList<CountryDetailsResp>>

    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String,
        @Query("units") units: String

    ): Call<WeatherResp>
}