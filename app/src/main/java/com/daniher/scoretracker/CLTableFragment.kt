package com.daniher.scoretracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CLTableFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var teamAdapter: CLTeamAdapter
    private lateinit var tournamentName: String
    private val teams = mutableListOf<CLTableItem>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tournamentName = it.getString("tournamentName") ?: ""
        }
    }

    companion object {
        fun newInstance(tournamentName: String): CLTableFragment {
            val fragment = CLTableFragment()
            val args = Bundle().apply {
                putString("tournamentName", tournamentName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cl_table, container, false)
        recyclerView = view.findViewById(R.id.recyclerView_teams)
        teamAdapter = CLTeamAdapter(teams)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }
        loadTeamsFromFirebase()
        return view
    }

    private fun loadTeamsFromFirebase() {
        val tournamentRef = db.collection("torneos").document(tournamentName)
        tournamentRef.collection("teams")
            .get()
            .addOnSuccessListener { result ->
                teams.clear()
                for (document in result) {
                    val team = document.toObject(CLTableItem::class.java)
                    val diferenciaGoles = team.golesFavor - team.golesContra
                    team.diferenciaGoles = diferenciaGoles
                    teams.add(team)
                }
                teams.sortWith(compareByDescending<CLTableItem> { it.puntos }.thenByDescending { it.diferenciaGoles })
                teamAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { _ ->
            }
    }

    fun reloadTeamsFromFirebase() {
        loadTeamsFromFirebase()
    }
}