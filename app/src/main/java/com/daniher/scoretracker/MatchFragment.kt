package com.daniher.scoretracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MatchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var matchAdapter: MatchAdapter
    private val matchList = mutableListOf<MatchItem>()
    private lateinit var tournamentName: String
    private lateinit var matchesListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_match, container, false)

        tournamentName = requireArguments().getString("tournamentName") ?: ""

        recyclerView = view.findViewById(R.id.matchRV)
        recyclerView.layoutManager = LinearLayoutManager(context)
        matchAdapter = MatchAdapter(matchList) { clickedMatchItem ->
            showDeleteConfirmationDialog(clickedMatchItem)
        }
        recyclerView.adapter = matchAdapter

        view.findViewById<Button>(R.id.addMatchButton)?.setOnClickListener {
            val dateET = view.findViewById<EditText>(R.id.dateET)
            val timeET = view.findViewById<EditText>(R.id.timeET)
            val team1ET = view.findViewById<EditText>(R.id.team1ET)
            val team2ET = view.findViewById<EditText>(R.id.team2ET)

            val date = dateET.text.toString()
            val time = timeET.text.toString()
            val team1 = team1ET.text.toString()
            val team2 = team2ET.text.toString()

            if (date.isNotEmpty() && time.isNotEmpty() && team1.isNotEmpty() && team2.isNotEmpty()) {
                val newMatch = MatchItem("", team1, team2, time, date)
                saveMatchToFirestore(newMatch)
                dateET.text.clear()
                timeET.text.clear()
                team1ET.text.clear()
                team2ET.text.clear()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val firestore = FirebaseFirestore.getInstance()
        val tournamentRef = firestore.collection("torneos").document(tournamentName)
        val matchesCollection = tournamentRef.collection("matches")

        matchesListener = matchesCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                matchList.clear()
                for (document in snapshot.documents) {
                    val matchItem = document.toObject(MatchItem::class.java)
                    matchItem?.id = document.id
                    matchList.add(matchItem!!)
                }
                matchAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        matchesListener.remove()
    }

    private fun saveMatchToFirestore(matchItem: MatchItem) {
        val firestore = FirebaseFirestore.getInstance()
        val tournamentRef = firestore.collection("torneos").document(tournamentName)
        val matchesCollection = tournamentRef.collection("matches")
        val newMatchDocument = matchesCollection.document()
        matchItem.id = newMatchDocument.id
        newMatchDocument.set(matchItem)
            .addOnSuccessListener {
            }
            .addOnFailureListener { _ ->
                // Handle error
            }
    }

    private fun showDeleteConfirmationDialog(matchItem: MatchItem) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Eliminación")
        builder.setMessage("¿Deseas eliminar este partido?")
        builder.setPositiveButton("Sí") { _, _ ->
            deleteMatchFromFirestore(matchItem)
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteMatchFromFirestore(matchItem: MatchItem) {
        val firestore = FirebaseFirestore.getInstance()
        val tournamentRef = firestore.collection("torneos").document(tournamentName)
        val matchesCollection = tournamentRef.collection("matches")
        matchesCollection.document(matchItem.id).delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { _ ->
                // Handle error
            }
    }

    companion object {
        fun newInstance(tournamentName: String): MatchFragment {
            val fragment = MatchFragment()
            val args = Bundle()
            args.putString("tournamentName", tournamentName)
            fragment.arguments = args
            return fragment
        }
    }
}