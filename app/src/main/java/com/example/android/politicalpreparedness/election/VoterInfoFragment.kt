package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    // Declare ViewModel using delegation
    private val viewModel : VoterInfoViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val application = activity.application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val arguments = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, arguments.argElectionId, arguments.argDivision)

        ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)
    }


    // Data binding
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        viewModel.url.observe(viewLifecycleOwner, Observer { url ->
            url?.let {
                viewModel.setUrl(null)
                //load URL intents
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        })

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root
    }

    //TODO: Create method to load URL intents

}