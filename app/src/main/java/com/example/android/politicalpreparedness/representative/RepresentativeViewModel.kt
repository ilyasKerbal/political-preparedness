package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun findRepresentatives() {
        viewModelScope.launch {
            // Try statement is used to catch Network Exceptions so the app does not
            // crash when attempting to load without a network connection
            try {
                val addressString = address.value?.toFormattedString()
                Log.i("Address", "Address: $addressString")
                if (addressString != null) {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(
                        addressString
                    )
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                }

            } catch (e: Exception) {
                _representatives.value = ArrayList()
            }
        }
    }


    //TODO: Create function get address from geo location

    fun setAddress(address: Address) {
        _address.value = address
    }



}
