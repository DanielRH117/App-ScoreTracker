package com.daniher.scoretracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(private val teams: MutableList<Team>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_TEAM = 1

    interface OnItemClickListener {
        fun onItemClick(team: Team)
    }

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        val equipoTextView: TextView = itemView.findViewById(R.id.equipoTextView)
        val pjTextView: TextView = itemView.findViewById(R.id.pjTextView)
        val gTextView: TextView = itemView.findViewById(R.id.gTextView)
        val ptsTextView: TextView = itemView.findViewById(R.id.ptsTextView)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val team = teams[position - 1]
                    listener.onItemClick(team)
                }
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pTV: TextView = itemView.findViewById(R.id.pTV)
        val eTV: TextView = itemView.findViewById(R.id.eTV)
        val pjTV: TextView = itemView.findViewById(R.id.pjTV)
        val gTV: TextView = itemView.findViewById(R.id.gTV)
        val ptsTV: TextView = itemView.findViewById(R.id.ptsTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val headerView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(headerView)
            }

            VIEW_TYPE_TEAM -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_team, parent, false)
                TeamViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
            }

            is TeamViewHolder -> {
                val team = teams[position - 1]
                holder.positionTextView.text = (position).toString()
                holder.equipoTextView.text = team.name
                holder.pjTextView.text = team.partidosJugados.toString()
                holder.gTextView.text = "${team.golesFavor}:${team.golesContra}"
                holder.ptsTextView.text = team.puntos.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return teams.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_TEAM
        }
    }

    fun setTeams(teams: List<Team>) {
        this.teams.clear()
        this.teams.addAll(teams)
        notifyDataSetChanged()
    }

    fun addTeam(team: Team) {
        teams.add(team)
        notifyItemInserted(teams.size - 1)
    }
}