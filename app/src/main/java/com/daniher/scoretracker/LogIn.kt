package com.daniher.scoretracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : AppCompatActivity() {

    private lateinit var etTournamentName: EditText
    private lateinit var btnConsL: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        btnAdmin.setOnClickListener { adminLeague() }

        etTournamentName = findViewById(R.id.et_ID)
        btnConsL = findViewById(R.id.btnConsL)


        btnConsL.setOnClickListener {
            val tournamentName = etTournamentName.text.toString().trim()
            if (tournamentName.isNotBlank()) {
                checkTournamentExists(tournamentName)
            } else {
                Toast.makeText(this, "Por favor, ingresa el nombre del torneo", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun adminLeague() {
        val intent = Intent(this, AdminLeague::class.java)
        startActivity(intent)
    }

    private fun checkTournamentExists(tournamentName: String) {
        val tournamentsRef = db.collection("torneos")
        tournamentsRef.whereEqualTo("name", tournamentName)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val intent = Intent(this, ConsultLeague::class.java)
                    intent.putExtra("tournamentName", tournamentName)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "El torneo \"$tournamentName\" no existe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error al consultar el torneo: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}