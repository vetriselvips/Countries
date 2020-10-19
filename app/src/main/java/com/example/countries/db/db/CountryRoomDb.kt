package com.example.countries.db.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.countries.db.dataConverter.CurrencyDataConverter
import com.example.countries.db.dataConverter.StringListDataConverter
import com.example.countries.db.dao.CountryDao
import com.example.countries.landingscreen.model.country.CountryDetailsResp

@Database(entities = [CountryDetailsResp::class], version = 1, exportSchema = false)
@TypeConverters(CurrencyDataConverter::class, StringListDataConverter::class)
abstract class CountryRoomDb : RoomDatabase() {

    abstract fun countryDao(): CountryDao

    companion object {
        @Volatile
        private var INSTANCE: CountryRoomDb? = null

        fun getDatabase(context: Context): CountryRoomDb {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CountryRoomDb::class.java,
                    "country_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}