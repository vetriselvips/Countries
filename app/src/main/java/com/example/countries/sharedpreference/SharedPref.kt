package com.example.countries.sharedpreference

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

     val SHARED_NAME = "COUNTRIES_SHARED_PREF"

    const val CUSTOM_LOCATION_PERMISSION = "ASK_LOCATION_PERMISSION"
    const val CUSTOM_GPS_PERMISSION = "ASK_GPS_PERMISSION"
    const val CALL_WEATHER_API = "CALL_WEATHER_API"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
    }

    fun getLocationValue(context: Context): String? {
        return getSharedPreferences(context).getString(CUSTOM_LOCATION_PERMISSION, null)
    }

    fun setLocationValue(context: Context, newValue: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(CUSTOM_LOCATION_PERMISSION, newValue)
        editor.commit()
    }
    fun getGpsValue(context: Context): String? {
        return getSharedPreferences(context).getString(CUSTOM_GPS_PERMISSION, null)
    }

    fun setGpsValue(context: Context, newValue: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(CUSTOM_GPS_PERMISSION, newValue)
        editor.commit()
    }
    fun getWeatherApiValue(context: Context): String? {
        return getSharedPreferences(context).getString(CALL_WEATHER_API, null)
    }

    fun setWeatherApiValue(context: Context, newValue: String?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(CALL_WEATHER_API, newValue)
        editor.commit()
    }
    fun clearData(context: Context){
        val editor= getSharedPreferences(context).edit()
        editor.clear()
        editor.commit()
    }
}