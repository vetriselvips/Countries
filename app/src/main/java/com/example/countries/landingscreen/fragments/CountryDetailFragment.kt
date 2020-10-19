package com.example.countries.landingscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.countries.R
import com.example.countries.databinding.FragmentCountryDetailBinding
import com.example.countries.landingscreen.activity.LandingActivity
import com.example.countries.landingscreen.model.country.CountryDetailsResp
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

        var binding: FragmentCountryDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_country_detail, container, false)
        val view: View = binding.root
        binding.countries = countryModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mtoolbar = activity?.findViewById(R.id.toolbar)!!
        (activity as LandingActivity).setSupportActionBar(mtoolbar)
        (activity as LandingActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as LandingActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as LandingActivity).supportActionBar?.title = countryModel.name

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
        }
        return true
    }

}