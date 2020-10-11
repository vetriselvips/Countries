package com.example.countries.landingscreen.model.weather

data class WeatherResp(
    var weather: ArrayList<WeatherDetailsResp>,
    var main: WeatherTempResp,
    var name: String
)