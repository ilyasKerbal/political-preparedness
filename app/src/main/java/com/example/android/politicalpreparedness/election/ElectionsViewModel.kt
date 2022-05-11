package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
        get() = _upcomingElections

    val savedElections = dataSource.getAllElections()

    init {
        getElections()
    }

    //TODO: Create live data val for saved elections

    private fun getElections() {
        viewModelScope.launch {
            try {
                _upcomingElections.value = CivicsApi.retrofitService.getElections().elections
            } catch (e: Exception) {
                _upcomingElections.value = ArrayList()
                Log.e("Test", "Network error: ${e.message}")
            }
        }
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo
        get() = _navigateToVoterInfo

    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigating() {
        _navigateToVoterInfo.value = null
    }

}