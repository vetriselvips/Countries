package com.example.countries.landingscreen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import com.example.countries.landingscreen.model.weather.WeatherResp
import com.example.countries.landingscreen.repository.CountryListRepo
import com.example.countries.resource.Resource
import com.example.countries.utils.AppConstants
import kotlinx.coroutines.launch

class CountryListViewModel(application: Application) : AndroidViewModel(application) {

    private val countryResp = MutableLiveData<Resource<ArrayList<CountryDetailsResp>>>()
    private val weatherResp = MutableLiveData<Resource<WeatherResp>>()

    private val countryListRepo = CountryListRepo()

    fun apiReqForFetchCountries() {
        viewModelScope.launch {
            countryListRepo.getCountryList(countryResp)
        }
    }

    fun getCountryList(): LiveData<Resource<ArrayList<CountryDetailsResp>>> {
        apiReqForFetchCountries()
        return countryResp
    }

    fun apiReqForFetchWeather(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            countryListRepo.getWeatherDetails(
                weatherResp,
                latitude,
                longitude,
                AppConstants.API_KEY,
                AppConstants.UNITS
            )
        }
    }

    fun getWeatherDetails(
        latitude: Double,
        longitude: Double
    ): LiveData<Resource<WeatherResp>> {
        apiReqForFetchWeather(latitude, longitude)
        return weatherResp
    }
}