package com.daniher.scoretracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference // Importar CollectionReference

class TournamentDetails : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tournamentName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_details)

        tournamentName = intent.getStringExtra("tournamentName") ?: ""

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val tabAdapter = TabAdapter(this)
        viewPager.adapter = tabAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Tabla"
                1 -> tab.text = "Partidos"
            }
        }.attach()

        val tournamentNameTextView = findViewById<TextView>(R.id.tournamentNameTextView)
        tournamentNameTextView.text = tournamentName

        val imageView: ImageView = findViewById(R.id.ic_deleteT)
        imageView.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar Torneo")
                .setMessage("¿Quieres eliminar este torneo?")
                .setPositiveButton("Sí") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    val tournamentRef = db.collection("torneos").document(tournamentName)
                    deleteCollection(tournamentRef.collection("teams"))
                    deleteCollection(tournamentRef.collection("matches"))
                    tournamentRef.delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "El torneo se borró exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Error al borrar el torneo: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .setNegativeButton("No") { _, _ ->
                }
                .show()
        }
    }

    private inner class TabAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TableFragment.newInstance(tournamentName)
                1 -> MatchFragment.newInstance(tournamentName)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

    private fun deleteCollection(collection: CollectionReference) {
        collection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
            }
        }
    }
}