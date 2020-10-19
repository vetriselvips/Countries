package com.example.countries.db.dataConverter

import androidx.room.TypeConverter
import com.example.countries.landingscreen.model.country.CountryCurrencies
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


class CurrencyDataConverter : Serializable {
    @TypeConverter
    fun fromListToString(currencyValue: ArrayList<CountryCurrencies?>?): String? {
        if (currencyValue == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<ArrayList<CountryCurrencies?>?>() {}.type
        return gson.toJson(currencyValue, type)
    }

    @TypeConverter
    fun fromStringToList(currencyValue: String?): ArrayList<CountryCurrencies>? {
        if (currencyValue == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<ArrayList<CountryCurrencies?>?>() {}.type
        return gson.fromJson<ArrayList<CountryCurrencies>>(currencyValue, type)
    }
}