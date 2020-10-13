package com.example.countries.landingscreen.listener

import com.example.countries.landingscreen.model.country.CountryDetailsResp

interface CountryItemClickListener {
    fun onCountryClickListener(
        model: CountryDetailsResp
    )
}