package com.daniher.scoretracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val txtName = findViewById<TextView>(R.id.etRName)
        val txtEmail = findViewById<TextView>(R.id.etRAddress)
        val txtPass1 = findViewById<TextView>(R.id.etRPassword1)
        val txtPass2 = findViewById<TextView>(R.id.etRPassword2)
        val btnCreateNew = findViewById<Button>(R.id.btnRCreate)
        btnCreateNew.setOnClickListener {
            val name = txtName.text.toString()
            val email = txtEmail.text.toString()
            val password1 = txtPass1.text.toString()
            val password2 = txtPass2.text.toString()
            registerUser(name, email, password1, password2)
        }
    }

    private fun registerUser(name: String, email: String, password1: String, password2: String) {
        if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password1 != password2) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser: FirebaseUser? = firebaseAuth.currentUser

                    if (currentUser != null) {
                        sendEmailVerification()
                        saveUserDataToFirestore(currentUser.uid, name, email)

                        Toast.makeText(
                            this,
                            "Registro exitoso. Por favor, verifica tu correo electrónico.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, AdminLeague::class.java)
                        intent.putExtra("userName", name) // Pasar el nombre del usuario
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Error al crear usuario: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserDataToFirestore(userId: String, name: String, email: String) {
        val userMap = hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email
        )

        db.collection("usuarios").document(userId)
            .set(userMap)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al guardar datos en Firestore: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun sendEmailVerification() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Correo de verificación enviado",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error al enviar el correo de verificación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}