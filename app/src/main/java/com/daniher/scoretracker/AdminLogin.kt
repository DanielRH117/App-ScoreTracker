package com.daniher.scoretracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AdminLogin : AppCompatActivity(), TournamentAdapter.OnItemClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var tournamentAdapter: TournamentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        db = FirebaseFirestore.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val userName = intent.getStringExtra("userName")
        if (userName != null) {
            val tvUserName = findViewById<TextView>(R.id.tvUserName)
            tvUserName.text = "Bienvenido, $userName"
        }

        if (uid != null) {
            val userRef = db.collection("usuarios").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    Log.d("AdminLogin", "Nombre de usuario: $name")
                    val tvUserName = findViewById<TextView>(R.id.tvUserName)

                    if (name != null) {
                        tvUserName.text = "Hola $name"
                    } else {
                        tvUserName.text = "Bienvenido"
                    }

                    if (name != null && email != null) {
                        loadAndDisplayTournaments(uid)
                    } else {
                        Toast.makeText(
                            this,
                            "Error: Datos de usuario incompletos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error: Documento de usuario no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val btnNewTournament = findViewById<Button>(R.id.btnNewTournament)
        btnNewTournament.setOnClickListener {
            navigateToNewTournament()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTournaments)
        tournamentAdapter = TournamentAdapter(ArrayList(), this)
        recyclerView.adapter = tournamentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(tournamentName: String) {
        val intent = Intent(this, TournamentDetails::class.java)
        intent.putExtra("tournamentName", tournamentName)
        startActivity(intent)
    }

    private fun loadAndDisplayTournaments(userId: String) {
        val userTournamentsCollection = db.collection("torneos")
            .whereEqualTo("userID", userId)

        userTournamentsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val tournamentsList = mutableListOf<String>()

                for (document in querySnapshot) {
                    val tournamentName = document.getString("name")
                    tournamentName?.let { tournamentsList.add(it) }
                }
                tournamentAdapter.setTournaments(tournamentsList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@AdminLogin,
                    "Error al cargar torneos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun navigateToNewTournament() {
        val intent = Intent(this, NewTournament::class.java)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            loadAndDisplayTournaments(uid)
        }
    }
}