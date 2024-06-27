package com.daniher.scoretracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class TableFragment : Fragment(), TeamAdapter.OnItemClickListener {

    private lateinit var editTextTeamName: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var tournamentName: String
    private val teams = mutableListOf<Team>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tournamentName = it.getString("tournamentName") ?: ""
        }
    }

    companion object {
        fun newInstance(tournamentName: String): TableFragment {
            val fragment = TableFragment()
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
        val view = inflater.inflate(R.layout.fragment_table, container, false)
        addButton = view.findViewById(R.id.btn_addT)
        editTextTeamName = view.findViewById(R.id.editText_teamName)
        recyclerView = view.findViewById(R.id.recyclerView_teams)
        teamAdapter = TeamAdapter(teams, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }
        addButton.setOnClickListener {
            val teamName = editTextTeamName.text.toString()
            addTeamToFirebase(teamName)
        }
        loadTeamsFromFirebase()
        return view
    }

    private fun addTeamToFirebase(teamName: String) {
        if (teamName.isNotBlank()) {
            val team = Team("", teamName, 0, 0, 0, 0, 0)
            if (tournamentName.isNotEmpty()) {
                val tournamentRef = db.collection("torneos").document(tournamentName)
                val newTeamRef = tournamentRef.collection("teams").document()
                val newTeamId = newTeamRef.id
                team.id = newTeamId
                newTeamRef.set(team)
                    .addOnSuccessListener {
                        teams.add(team)
                        teamAdapter.notifyDataSetChanged()
                        editTextTeamName.text.clear()
                    }
                    .addOnFailureListener { _ ->
                    }
            } else {
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Por favor, ingresa el nombre del equipo.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadTeamsFromFirebase() {
        val tournamentRef = db.collection("torneos").document(tournamentName)
        tournamentRef.collection("teams")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val team = document.toObject(Team::class.java)
                    val diferenciaGoles = team.golesFavor - team.golesContra
                    team.diferenciaGoles = diferenciaGoles
                    teams.add(team)
                }
                teams.sortWith(compareByDescending<Team> { it.puntos }.thenByDescending { it.diferenciaGoles })
                teamAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { _ ->
            }
    }

    override fun onItemClick(team: Team) {
        showEditTeamDialog(team)
    }

    private fun showEditTeamDialog(team: Team) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Editar Equipo")

        val view = layoutInflater.inflate(R.layout.dialog_edit_team, null)
        builder.setView(view)

        val pjEditText: EditText = view.findViewById(R.id.editText_pj)
        val golesFavorEditText: EditText = view.findViewById(R.id.ETgolesFavor)
        val golesContraEditText: EditText = view.findViewById(R.id.ETgolesContra)
        val ptsEditText: EditText = view.findViewById(R.id.editText_pts)
        pjEditText.setText(team.partidosJugados.toString())
        golesFavorEditText.setText(team.golesFavor.toString())
        golesContraEditText.setText(team.golesContra.toString())
        ptsEditText.setText(team.puntos.toString())

        builder.setPositiveButton("Guardar") { _, _ ->
            val pj = pjEditText.text.toString().toIntOrNull() ?: 0
            val golesFavor = golesFavorEditText.text.toString().toIntOrNull() ?: 0
            val golesContra = golesContraEditText.text.toString().toIntOrNull() ?: 0
            val pts = ptsEditText.text.toString().toIntOrNull() ?: 0

            team.partidosJugados = pj
            team.golesFavor = golesFavor
            team.golesContra = golesContra
            team.puntos = pts

            val tournamentRef = db.collection("torneos").document(tournamentName)
            tournamentRef.collection("teams").document(team.id).set(team)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Equipo actualizado correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadTeamsFromFirebase()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar el equipo: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        builder.setNeutralButton("Borrar Equipo") { _, _ ->
            val tournamentRef = db.collection("torneos").document(tournamentName)
            tournamentRef.collection("teams").document(team.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Equipo borrado correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    reloadTeamsFromFirebase()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error al borrar el equipo: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun reloadTeamsFromFirebase() {
        loadTeamsFromFirebase()
    }
}