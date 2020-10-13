package com.example.countries.landingscreen.fragments

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.ahmadrosid.svgloader.SvgLoader
import com.example.countries.R
import com.example.countries.landingscreen.activity.LandingActivity
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import kotlinx.android.synthetic.main.fragment_country_detail.*
import kotlinx.android.synthetic.main.fragment_country_detail.view.*
import java.text.NumberFormat
import java.util.*

class CountryDetailFragment : Fragment() {

    private lateinit var countryModel: CountryDetailsResp
    private lateinit var mtoolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        countryModel = arguments?.get("countryModel") as CountryDetailsResp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_country_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mtoolbar = activity?.findViewById(R.id.toolbar)!!
        (activity as LandingActivity).setSupportActionBar(mtoolbar)
        (activity as LandingActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as LandingActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as LandingActivity).supportActionBar?.title = countryModel.name
        populateDataFromArguments(view, countryModel)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
        }
        return true
    }

    private fun populateDataFromArguments(view: View, countryModel: CountryDetailsResp) {
        SvgLoader.pluck()
            .with(context as Activity?)
            .setPlaceHolder(
                R.drawable.ic_no_image_placeholder,
                R.drawable.ic_no_image_placeholder
            )
            .load(countryModel.flag, view.ivFlag)

        if (countryModel.capital.isNullOrEmpty()) {
            clCountryCapital.visibility = View.GONE
        } else {
            clCountryCapital.visibility = View.VISIBLE
            view.tvCountryCapital.text = countryModel.capital
        }
        if (countryModel.currencies.isNullOrEmpty() && countryModel.currencies.size <= 0) {
            clCountryCurrency.visibility = View.GONE
        } else {
            clCountryCurrency.visibility = View.VISIBLE
            val currencyCode = countryModel.currencies.get(0).code ?: "-"
            val currencyName = countryModel.currencies.get(0).name ?: "-"
            view.tvCountryCurrency.text =
                "$currencyName($currencyCode)"

        }
        if (countryModel.region.isNullOrEmpty() && countryModel.subregion.isNullOrEmpty()) {
            clCountryRegion.visibility = View.GONE
        } else {
            clCountryRegion.visibility = View.VISIBLE
            view.tvCountryRegion.text = countryModel.region +
                    " / " +
                    countryModel.subregion

        }
        if (countryModel.population.isNullOrEmpty() || countryModel.population == "0") {
            clCountryPopulation.visibility = View.GONE
        } else {
            clCountryPopulation.visibility = View.VISIBLE
            view.tvCountryPopulation.text = getFormattedPopulation(countryModel.population)
        }
        if (countryModel.timezones.isNullOrEmpty() && countryModel.timezones.size <= 0) {
            clCountryTimeZone.visibility = View.GONE
        } else {
            clCountryTimeZone.visibility = View.VISIBLE
            view.tvCountryTimeZone.text = countryModel.timezones.get(0)
        }
    }

    private fun getFormattedPopulation(population: String): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(population.toInt())
            .toString()
    }


}