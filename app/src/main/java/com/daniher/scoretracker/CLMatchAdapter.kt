package com.daniher.scoretracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CLMatchAdapter(private val matchList: List<MatchItem>) : RecyclerView.Adapter<CLMatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cl_match_item, parent, false)
        return MatchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val currentItem = matchList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = matchList.size

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val team1TextView: TextView = itemView.findViewById(R.id.CLteam1TV)
        private val team2TextView: TextView = itemView.findViewById(R.id.CLteam2TV)
        private val timeTextView: TextView = itemView.findViewById(R.id.CLtimeTV)
        private val dateTextView: TextView = itemView.findViewById(R.id.CLdateTV)

        fun bind(matchItem: MatchItem) {
            team1TextView.text = matchItem.team1
            team2TextView.text = matchItem.team2
            timeTextView.text = matchItem.time
            dateTextView.text = matchItem.date
        }
    }
}