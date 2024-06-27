package com.daniher.scoretracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TournamentAdapter(
    private val tournaments: MutableList<String>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {
    class TournamentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tournamentNameTextView: TextView = itemView.findViewById(R.id.tournamentNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tournament, parent, false)
        return TournamentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournamentName = tournaments[position]
        holder.tournamentNameTextView.text = tournamentName

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(tournamentName)
        }
    }

    override fun getItemCount(): Int {
        return tournaments.size
    }

    fun setTournaments(tournaments: List<String>) {
        this.tournaments.clear()
        this.tournaments.addAll(tournaments)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(tournamentName: String)
    }
}