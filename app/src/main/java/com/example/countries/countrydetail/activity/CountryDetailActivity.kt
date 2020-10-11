package com.example.countries.countrydetail.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ahmadrosid.svgloader.SvgLoader
import com.example.countries.R
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import com.example.countries.utils.AppConstants
import kotlinx.android.synthetic.main.activity_country_detail.*
import kotlinx.android.synthetic.main.toolbar_search_header.*
import java.text.NumberFormat
import java.util.*

class CountryDetailActivity : AppCompatActivity() {
    private lateinit var countryDetail: CountryDetailsResp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)
        getIntentFromActivity()
        InitializeView()
    }

    private fun getIntentFromActivity() {
        if (intent.hasExtra(AppConstants.INTENT_COUNTRY_DETAIL)) {
            countryDetail =
                intent.getSerializableExtra(AppConstants.INTENT_COUNTRY_DETAIL) as CountryDetailsResp
        }
    }

    private fun InitializeView() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = countryDetail.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        populateData()
    }

    private fun populateData() {
        SvgLoader.pluck().with(this)
            .setPlaceHolder(R.drawable.ic_no_image_placeholder, R.drawable.ic_no_image_placeholder)
            .load(countryDetail.flag, ivFlag)
        if (countryDetail.capital.isNullOrEmpty()) {
            tvCountryCapital.text = "--"
        } else {
            tvCountryCapital.text = countryDetail.capital
        }
        tvCountryRegion.text = countryDetail.region ?: "-" +
                " / " +
                countryDetail.subregion ?: "-"
        tvCountryPopulation.text = getFormattedPopulation(countryDetail.population ?: "-")
        if (countryDetail.currencies.size > 0) {
            val currencyCode = countryDetail.currencies.get(0).code ?: "-"
            val currencyName = countryDetail.currencies.get(0).name ?: "-"
            tvCountryCurrency.text =
                "$currencyName($currencyCode)"
        } else {
            tvCountryCurrency.text = "--"
        }
        if (countryDetail.timezones.isNotEmpty()) {
            tvCountryTimeZone.text = countryDetail.timezones.get(0)
        } else {
            tvCountryTimeZone.text = "--"
        }
    }

    private fun getFormattedPopulation(population: String): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(population.toInt())
            .toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}