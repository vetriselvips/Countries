package com.example.countries.landingscreen.repository

import androidx.lifecycle.MutableLiveData
import com.example.countries.extensions.setError
import com.example.countries.extensions.setLoading
import com.example.countries.extensions.setSuccess
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import com.example.countries.landingscreen.model.weather.WeatherResp
import com.example.countries.network.ApiClient
import com.example.countries.network.ApiInterface
import com.example.countries.resource.Resource
import com.example.countries.utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountryListRepo {

    fun getCountryList(countryResp: MutableLiveData<Resource<ArrayList<CountryDetailsResp>>>) {
        val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        countryResp.setLoading()
        val apiResp: Call<ArrayList<CountryDetailsResp>> = apiInterface.getCountryList()
        apiResp.enqueue(object : Callback<ArrayList<CountryDetailsResp>> {
            override fun onFailure(call: Call<ArrayList<CountryDetailsResp>>, t: Throwable) {
                countryResp.setError(AppConstants.NETWORK_CALL_FAILED)
            }

            override fun onResponse(
                call: Call<ArrayList<CountryDetailsResp>>,
                response: Response<ArrayList<CountryDetailsResp>>
            ) {
                if (response.isSuccessful) {
                    countryResp.setSuccess(response.body())
                } else {
                    countryResp.setError(AppConstants.NETWORK_CALL_FAILED)
                }
            }
        })
    }

    fun getWeatherDetails(
        weatherResp: MutableLiveData<Resource<WeatherResp>>,
        latitude: Double, longitude: Double, API_KEY: String, units: String
    ) {
        val weatherApiInterface: ApiInterface =
            ApiClient.getWeatherClient().create(ApiInterface::class.java)
        weatherResp.setLoading()
        val apiResp: Call<WeatherResp> =
            weatherApiInterface.getWeather(latitude, longitude, API_KEY, units)
        apiResp.enqueue(object : Callback<WeatherResp> {
            override fun onFailure(call: Call<WeatherResp>, t: Throwable) {
                weatherResp.setError(AppConstants.NETWORK_CALL_FAILED)
            }

            override fun onResponse(
                call: Call<WeatherResp>,
                response: Response<WeatherResp>
            ) {
                if (response.isSuccessful) {
                    weatherResp.setSuccess(response.body())
                } else {
                    weatherResp.setError(AppConstants.NETWORK_CALL_FAILED)
                }
            }
        })
    }

}