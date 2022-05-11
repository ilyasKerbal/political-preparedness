package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

private const val PERMISSION_REQUEST_CODE = 16

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
    }

    private val viewModel by viewModels<RepresentativeViewModel>()
    private lateinit var binding: FragmentRepresentativeBinding

    private var snackbar: Snackbar? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

//        viewModel.address.observe(viewLifecycleOwner) { t ->
//            t?.let {
//                viewModel.findRepresentatives()
//            }
//        }

        binding.representativesList.adapter = RepresentativeListAdapter()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions())
                getLocation()
        }

        binding.buttonSearch.setOnClickListener {
            val address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )
            Log.i("Address", "Address: $address")
            viewModel.setAddress(address)
            viewModel.findRepresentatives()
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.e("Test", "Yes called: ${locationResult?.lastLocation}")
                locationResult ?: return
                for (location in locationResult.locations){
                    viewModel.setAddress(geoCodeLocation(location))
                }
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Handle location permission result to get location on permission granted

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startLocationUpdates()
                    // Permission is granted. Continue the action or workflow
                    getLocation()
                } else {
                    // Permission denied.
                    snackbar = Snackbar.make(
                        binding.root,
                        R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
                    )
                    snackbar?.setAction(R.string.settings) {
                        // Displays App settings screen.
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }?.show()
                }
                return
            }
            else -> {
                // Other stuff here
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        if (isPermissionGranted()) {
            return true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
            return false
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //Get location from LocationServices
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                val address = location?.let { geoCodeLocation(it) }
                address?.let {
                    viewModel.setAddress(address)
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
        stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY), locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}