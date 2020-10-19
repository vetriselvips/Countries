package com.example.countries.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.countries.landingscreen.model.country.CountryDetailsResp

@Dao
interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(name: CountryDetailsResp)

    @Query("SELECT * FROM country_resp")
    suspend fun getAllCountry(): List<CountryDetailsResp>

    @Query("DELETE FROM country_resp")
    suspend fun deleteAll()


}