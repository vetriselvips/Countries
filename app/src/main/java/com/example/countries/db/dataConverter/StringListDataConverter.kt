package com.example.countries.db.dataConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


class StringListDataConverter : Serializable {
    @TypeConverter
    fun fromListToString(arrString: ArrayList<String?>?): String? {
        if (arrString == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.toJson(arrString, type)
    }

    @TypeConverter
    fun fromStringToList(arrString: String?): ArrayList<String>? {
        if (arrString == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson<ArrayList<String>>(arrString, type)
    }
}