package com.example.countries.landingscreen.model.country

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ahmadrosid.svgloader.SvgLoader
import com.example.countries.R
import com.example.countries.db.dataConverter.CurrencyDataConverter
import com.example.countries.db.dataConverter.StringListDataConverter
import java.io.Serializable
import java.text.NumberFormat
import java.util.*


@Entity(tableName = "country_resp")
data class CountryDetailsResp(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "flag")
    val flag: String,
    @ColumnInfo(name = "alpha2Code")
    val alpha2Code: String,
    @ColumnInfo(name = "capital")
    val capital: String?,
    @ColumnInfo(name = "region")
    val region: String?,
    @ColumnInfo(name = "subregion")
    val subregion: String?,
    @ColumnInfo(name = "population")
    val population: String?,
    @TypeConverters(CurrencyDataConverter::class)
    @ColumnInfo(name = "currencies")
    val currencies: ArrayList<CountryCurrencies>,
    @TypeConverters(StringListDataConverter::class)
    @ColumnInfo(name = "timezones")
    val timezones: ArrayList<String>
) : Serializable {
    companion object {
        @JvmStatic
        @BindingAdapter("flag")
        fun loadImage(view: AppCompatImageView, imageUrl: String?) {
            SvgLoader.pluck()
                .with(view.context as Activity)
                .setPlaceHolder(
                    R.drawable.ic_no_image_placeholder,
                    R.drawable.ic_no_image_placeholder
                )
                .load(imageUrl, view)

        }

        @JvmStatic
        @BindingAdapter("population")
        fun getFormattedPopulation(view: TextView, population: String) {
            view.text =
                NumberFormat.getNumberInstance(Locale.getDefault()).format(population.toInt())
                    .toString()
        }
    }

}
