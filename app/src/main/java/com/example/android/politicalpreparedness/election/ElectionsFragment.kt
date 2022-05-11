package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    // Declare ViewModel
    private val viewModel : ElectionsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val application = activity.application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource)

        ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
    }

    // Data binding
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.doneNavigating()
            }
        })

        val upcomingElectionAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onElectionClicked(it)
        })

        val savedElectionAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onElectionClicked(it)
        })

        binding.savedElectionsList.adapter = savedElectionAdapter
        binding.upcomingElectionsList.adapter = upcomingElectionAdapter
        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}