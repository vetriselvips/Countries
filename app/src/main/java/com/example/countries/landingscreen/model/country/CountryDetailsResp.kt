package com.example.countries.landingscreen.model.country

import java.io.Serializable

data class CountryDetailsResp(
    val name: String,
    val flag: String,
    val alpha2Code: String,
    val capital: String?,
    val region: String?,
    val subregion: String?,
    val population: String?,
    val currencies: ArrayList<CountryCurrencies>,
    val timezones: List<String>
) : Serializable
