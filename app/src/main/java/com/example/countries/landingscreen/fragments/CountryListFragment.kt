package com.example.countries.landingscreen.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.countries.R
import com.example.countries.landingscreen.activity.LandingActivity
import com.example.countries.landingscreen.adapter.CountryListAdapter
import com.example.countries.landingscreen.listener.CountryItemClickListener
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import com.example.countries.landingscreen.model.weather.WeatherResp
import com.example.countries.landingscreen.viewmodel.CountryListViewModel
import com.example.countries.resource.ResourceState
import com.example.countries.sharedpreference.SharedPref
import com.example.countries.utils.AppConstants
import com.example.countries.utils.NetworkUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_country_list.*
import kotlinx.android.synthetic.main.fragment_country_list.view.*
import kotlinx.android.synthetic.main.layout_weather.*
import kotlin.math.roundToInt


class CountryListFragment : Fragment(), View.OnClickListener {

    private lateinit var countryListViewModel: CountryListViewModel
    private var countryList: ArrayList<CountryDetailsResp> = ArrayList()
    private val ACCESS_FINE_LOCATION_INTENT_ID = 3
    lateinit var mGoogleApiClient: GoogleApiClient
    var REQUEST_CHECK_SETTINGS = 100
    private lateinit var countryListAdapter: CountryListAdapter
    private lateinit var searchView: SearchView
    private lateinit var mcontext: Context
    private lateinit var mtoolbar: Toolbar
    private var navController: NavController? = null
    private lateinit var fragView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragView = inflater.inflate(R.layout.fragment_country_list, container, false)
        return fragView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        InitializeView()
        InitializeViewModel()
        initGoogleAPIClient()
        checkRuntimePermissions()
    }

    private fun InitializeViewModel() {
        countryListViewModel = ViewModelProvider(this).get(CountryListViewModel::class.java)
        observeFetchDbData()
    }

    private fun observeFetchDbData() {
        countryListViewModel.getAllCountry().observe(requireActivity(), Observer {
            it.let { it1 ->
                checkDataAvailabilityFromDb(it1)
            }
        })
    }

    private fun checkDataAvailabilityFromDb(data: ArrayList<CountryDetailsResp>) {
        if (NetworkUtils.isNetworkAvailable(mcontext)) {
            fragView.tvNoItemFound.text = getString(R.string.no_item_found)
            if (data.size <= 0) {
                callCountryApi()
            } else {
                updateList(data)
            }
        } else if (data.size <= 0) {
            fragView.rvCountryList.visibility = View.GONE
            fragView.tvNoItemFound.visibility = View.VISIBLE
            fragView.tvNoItemFound.text = getString(R.string.no_internet)
        } else {
            fragView.tvNoItemFound.text = getString(R.string.no_item_found)
            updateList(data)
        }
    }

    private fun InitializeView() {
        mcontext = activity?.applicationContext!!
        mtoolbar = activity?.findViewById(R.id.toolbar)!!
        (activity as LandingActivity).setSupportActionBar(mtoolbar)
        (activity as LandingActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as LandingActivity).supportActionBar?.title =
            getString(R.string.all_countries)
        val layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        fragView.rvCountryList.layoutManager = layoutManager
        fragView.rvCountryList.itemAnimator = DefaultItemAnimator()
        countryListAdapter =
            CountryListAdapter(countryList, countryItemClick)
        fragView.rvCountryList.adapter = countryListAdapter

    }

    override fun onStart() {
        super.onStart()
        var registerBroadastAction = IntentFilter()
        registerBroadastAction.addAction(AppConstants.LOCATION_BROADCAST_ACTION)
        registerBroadastAction.addAction(AppConstants.NETWORK_BROADCAST_ACTION)
        activity?.registerReceiver(
            broadcastReceiver,
            registerBroadastAction
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(broadcastReceiver)
        SharedPref.clearData(mcontext)
    }

    val broadcastReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.action?.let { actions ->
                    if (actions.contains(AppConstants.LOCATION_BROADCAST_ACTION)) {
                        checkGpsStatus()
                    } else if (actions.contains(AppConstants.NETWORK_BROADCAST_ACTION) &&
                        (NetworkUtils.isNetworkAvailable(context))
                    ) {
                        if (fragView.tvNoItemFound.text.equals(getString(R.string.no_internet))) {
                            countryListViewModel.apiReqForFetchCountries()
                            callCountryApi()
                        }
                        getLocationDetails()
                    }
                }
            }
        }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            androidx.appcompat.R.id.search_close_btn -> {
                if (searchView.query.isEmpty()) {
                    searchView.isIconified = true
                } else {
                    filterListAndUpdateUi("")
                    searchView.setQuery("", false)
                }
            }
        }
    }

    private fun checkGpsStatus() {
        val locationManager = mcontext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocationDetails()
        } else {
            // gps is off
        }
    }


    //region CountryList
    private fun callCountryApi() {
        if (countryList.size <= 0) {
            countryListViewModel.apiReqForFetchCountries()
            countryListViewModel.getCountryList().observe(requireActivity(), Observer {
                when (it.state) {
                    ResourceState.LOADING -> {
                        fragView.pbCountryList.visibility = View.VISIBLE
                    }
                    ResourceState.SUCCESS -> {
                        fragView.pbCountryList.visibility = View.GONE
                        it.data?.let { resp ->
                            if (countryList.size > 0) {
                                countryListViewModel.insertIntoDb(resp)
                            }
                            updateList(resp)

                        }
                    }
                    ResourceState.ERROR -> {
                        fragView.pbCountryList.visibility = View.GONE
                        it.message?.let { it1 -> showToast(it1) }
                        fragView.rvCountryList.visibility = View.GONE
                        fragView.tvNoItemFound.visibility = View.VISIBLE
                    }
                }
            })
        } else {
            updateList(countryList)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun updateList(countryDetail: List<CountryDetailsResp>) {
        fragView.tvNoItemFound.text = getString(R.string.no_item_found)
        if (countryDetail.size < 0) {
            fragView.rvCountryList.visibility = View.GONE
            fragView.tvNoItemFound.visibility = View.VISIBLE
        } else {
            fragView.rvCountryList.visibility = View.VISIBLE
            fragView.tvNoItemFound.visibility = View.GONE
            countryList.clear()
            countryList.addAll(countryDetail)
        }
        fragView.rvCountryList.adapter?.notifyDataSetChanged()
    }

    private val countryItemClick = object : CountryItemClickListener {
        override fun onCountryClickListener(
            model: CountryDetailsResp
        ) {
            navigateToCountryDetailScreen(model)
        }
    }

    private fun navigateToCountryDetailScreen(model: CountryDetailsResp) {
        val bundle = bundleOf("countryModel" to model)
        navController!!.navigate(
            R.id.action_goto_countryDetailFragment,
            bundle
        )
    }
    //endregion

    //region LocationPermissionAndGps
    private fun initGoogleAPIClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mcontext)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    private fun checkRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                askPermissionFromUserForAccessingLocation()
            } else {
                enableGPS()
            }
        } else {
            enableGPS()
        }
    }

    private fun askPermissionFromUserForAccessingLocation() {
        if (SharedPref.getLocationValue(mcontext).isNullOrEmpty()) {
            val dialogBuilder = AlertDialog.Builder(requireActivity())
            dialogBuilder.setMessage(getString(R.string.askLocationAlertMsg))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, id ->
                    requestLocationPermissionForWeather()
                }
                .setNegativeButton(
                    getString(R.string.not_now)
                ) { dialog, id ->
                    dialog.cancel()
                    SharedPref.setLocationValue(mcontext, getString(R.string.not_now))
                    clWeatherDetails.visibility = View.GONE
                    if (NetworkUtils.isNetworkAvailable(mcontext)) {
                        clLocationError.visibility = View.VISIBLE
                        tvWeatherError.text = getString(R.string.permission_denied_msg)
                        tvNoItemFound.text = getString(R.string.no_item_found)
                    }
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.askLocationAlertTitle))
            alert.show()
        } else if (SharedPref.getLocationValue(mcontext)!!.contains(getString(R.string.not_now))) {
            // do nothing
        }
    }

    private fun requestLocationPermissionForWeather() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_INTENT_ID
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_INTENT_ID
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION_INTENT_ID) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (mGoogleApiClient == null) {
                    initGoogleAPIClient()
                    enableGPS()
                } else {
                    enableGPS()
                }
            } else {
                checkRuntimePermissions()
            }
        }
    }


    private fun enableGPS() {
        if (SharedPref.getGpsValue(mcontext).isNullOrEmpty()) {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result: Task<LocationSettingsResponse> =
                LocationServices.getSettingsClient(requireActivity())
                    .checkLocationSettings(builder.build())

            result.addOnCompleteListener { task ->
                try {
                    task.getResult(ApiException::class.java)
                    getLocationDetails()

                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.SUCCESS -> {
                            getLocationDetails()
                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvable: ResolvableApiException =
                                    exception as ResolvableApiException
                                resolvable.startResolutionForResult(
                                    requireActivity(), LocationRequest.PRIORITY_HIGH_ACCURACY
                                )
                            } catch (e: Exception) {
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        }
                    }
                }
            }
        } else {
            //do nothing
        }
    }

    private fun getLocationDetails() {
        try {
            if (NetworkUtils.isNetworkAvailable(mcontext)) {
                fragView.tvNoItemFound.text = getString(R.string.no_item_found)
                fragView.tvNoItemFound.visibility = View.GONE
                clLocationError.visibility = View.VISIBLE
                getLocationAndCallApi()
            } else {
                //do nothing
            }
        } catch (e: java.lang.Exception) {
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocationAndCallApi() {
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    callWeatherApi(0.0, 0.0)
                    showToast(getString(R.string.no_location))
                } else {
                    callWeatherApi(location.latitude, location.longitude)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    getLocationDetails()
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    SharedPref.setGpsValue(mcontext, getString(R.string.gps_no_thanks))
                    clWeatherDetails.visibility = View.GONE
                    if (NetworkUtils.isNetworkAvailable(mcontext)) {
                        clLocationError.visibility = View.VISIBLE
                        tvWeatherError.text = getString(R.string.weather_error_msg)
                    }
                    showToast(getString(R.string.gpsDeniedToast))
                }
            }
        }
    }
    //endregion

    //region Weather
    private fun callWeatherApi(latitude: Double, longitude: Double) {
        countryListViewModel.getWeatherDetails(latitude, longitude)
            .observe(requireActivity(), Observer {
                when (it.state) {
                    ResourceState.LOADING -> {
                        pbWeather.visibility = View.VISIBLE
                    }
                    ResourceState.SUCCESS -> {
                        pbWeather.visibility = View.GONE
                        it.data?.let { resp ->
                            populateWeatherData(resp)
                        }
                    }
                    ResourceState.ERROR -> {
                        pbWeather.visibility = View.GONE
                        clWeatherDetails.visibility = View.GONE
                        clLocationError.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun populateWeatherData(resp: WeatherResp) {
        clWeatherDetails.visibility = View.VISIBLE
        clLocationError.visibility = View.GONE
        tvCityName.text = resp.name
        tvMinAndMaxTemp.text =
            getString(R.string.min) + roundToIntWithCelcius(resp.main.temp_min) + " - ${getString(
                R.string.max
            )} " + roundToIntWithCelcius(
                resp.main.temp_max
            )
        tvCurrentTemp.text = roundToIntWithCelcius(resp.main.temp)

        Picasso.get()
            .load(AppConstants.IMAGE_BASEURL + resp.weather.get(0).icon + AppConstants.IMAGE_ENDURL)
            .into(ivWeather)
        tvFeelsLikeTemp.text =
            getString(R.string.feels_like) + roundToIntWithCelcius(resp.main.feels_like)
        tvWeatherDesc.text = resp.weather.get(0).main
    }

    private fun roundToIntWithCelcius(temperature: Double): String {
        return " " + temperature.roundToInt() + "℃"
    }
    //endregion

    //region ToolbarSearchView
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_country, menu)
        val searchItem = menu.findItem(R.id.itemSearch)
        searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_country)
        searchView.setOnQueryTextListener(searchListener)
        val clearButton =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        clearButton.setOnClickListener(this)
        searchItem.setOnActionExpandListener(searchListenExpand)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private val searchListenExpand = object : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
            return true
        }

        override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
            filterListAndUpdateUi("")
            return true
        }
    }

    private val searchListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            filterListAndUpdateUi(query)
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            filterListAndUpdateUi(query)
            return true
        }
    }

    private fun filterListAndUpdateUi(query: String?) {
        countryListAdapter.filter.filter(query)
        if (countryListAdapter.filteredCountryList.size < 1) {
            fragView.rvCountryList.visibility = View.GONE
            fragView.tvNoItemFound.visibility = View.VISIBLE
        } else {
            fragView.rvCountryList.visibility = View.VISIBLE
            fragView.tvNoItemFound.visibility = View.GONE
        }
    }
    //endregion
}