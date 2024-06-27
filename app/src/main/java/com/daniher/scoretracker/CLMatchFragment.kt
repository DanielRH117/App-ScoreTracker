package com.daniher.scoretracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CLMatchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clMatchAdapter: CLMatchAdapter
    private val matchList = mutableListOf<MatchItem>()
    private lateinit var tournamentName: String
    private lateinit var matchesListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clmatch, container, false)

        tournamentName = requireArguments().getString("tournamentName") ?: ""

        recyclerView = view.findViewById(R.id.rvMatchCL)
        recyclerView.layoutManager = LinearLayoutManager(context)
        clMatchAdapter = CLMatchAdapter(matchList)
        recyclerView.adapter = clMatchAdapter
        return view
    }

    override fun onStart() {
        super.onStart()
        val firestore = Firebase.firestore
        val tournamentRef = firestore.collection("torneos").document(tournamentName)
        val matchesCollection = tournamentRef.collection("matches")

        matchesListener = matchesCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                matchList.clear()
                for (document in snapshot.documents) {
                    val matchItem = document.toObject(MatchItem::class.java)?.apply {
                        id = document.id
                    }
                    matchList.add(matchItem!!)
                }
                clMatchAdapter.notifyItemRangeInserted(0, matchList.size)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        matchesListener.remove()
    }

    companion object {
        fun newInstance(tournamentName: String): CLMatchFragment {
            val fragment = CLMatchFragment()
            val args = Bundle().apply {
                putString("tournamentName", tournamentName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}