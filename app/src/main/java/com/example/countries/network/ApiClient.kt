package com.example.countries.network

import com.example.countries.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    companion object {
        fun getClient(): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

            var gson: Gson = GsonBuilder().create()

            return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(AppConstants.BASE_URL_COUNTRY)
                .build()
        }

        fun getWeatherClient(): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

            var gson: Gson = GsonBuilder().create()

            return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(AppConstants.BASE_URL_WEATHER)
                .build()
        }
    }


}