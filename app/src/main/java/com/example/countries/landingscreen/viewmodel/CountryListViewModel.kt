package com.example.countries.landingscreen.viewmodel

import android.app.Application
import android.content.Context
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
    private val context: Context = application
    var dataFromDbResp = MutableLiveData<ArrayList<CountryDetailsResp>>()


    fun apiReqForFetchCountries() {
        viewModelScope.launch {
            countryListRepo.getCountryList(countryResp)
        }
    }

    fun getCountryList(): LiveData<Resource<ArrayList<CountryDetailsResp>>> {
        return countryResp
    }

    fun insertIntoDb(resp: ArrayList<CountryDetailsResp>) {
        viewModelScope.launch {
            countryListRepo.insertIntoDb(resp, context)
        }
    }

    fun fetchAllCountry() {
        viewModelScope.launch {
            dataFromDbResp.value?.clear()
            dataFromDbResp.value =
                countryListRepo.getAllDataFromDb(context)
        }

    }

    fun getAllCountry(): LiveData<ArrayList<CountryDetailsResp>>{
        fetchAllCountry()
        return dataFromDbResp
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