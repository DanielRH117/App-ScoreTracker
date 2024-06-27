package com.daniher.scoretracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewTournament : AppCompatActivity() {

    private lateinit var etTournamentName: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_tournament)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        etTournamentName = findViewById(R.id.etTournamentName)

        val btnCreateTournament = findViewById<Button>(R.id.btnCreateTournament)
        btnCreateTournament.setOnClickListener {
            val tournamentName = etTournamentName.text.toString().trim()
            if (tournamentName.isNotEmpty()) {
                checkTournamentNameExists(tournamentName)
            } else {
                Toast.makeText(
                    this,
                    "Ingrese el nombre del torneo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkTournamentNameExists(tournamentName: String) {
        db.collection("torneos")
            .document(tournamentName)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    saveTournamentToFirestore(tournamentName)
                } else {
                    Toast.makeText(
                        this,
                        "Ya existe un torneo con el mismo nombre",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al verificar el nombre del torneo",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w("NewTournament", "Error checking tournament name", e)
            }
    }

    private fun saveTournamentToFirestore(tournamentName: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val tournamentData = hashMapOf(
                "name" to tournamentName,
                "userID" to uid
            )

            db.collection("torneos")
                .document(tournamentName)
                .set(tournamentData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Torneo creado y guardado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    val adminLoginIntent = Intent(this, AdminLogin::class.java)
                    adminLoginIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(adminLoginIntent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al crear y guardar el torneo",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w("NewTournament", "Error adding document", e)
                }
        } else {
            Toast.makeText(
                this,
                "Error al obtener el UID del usuario",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}