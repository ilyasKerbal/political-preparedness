package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return from(parent)
    }

    //Bind ViewHolder

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val electionItem = getItem(position) as Election
        holder.bind(clickListener, electionItem)
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemElectionBinding.inflate(layoutInflater, parent, false)

            return ElectionViewHolder(binding)
        }
    }
}

class ElectionViewHolder(val binding: ListItemElectionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(clickListener: ElectionListener, item: Election) {
        binding.election = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

class ElectionListener(val clickListener: (election: Election) -> Unit){
    fun onClick(election: Election) = clickListener(election)
}

