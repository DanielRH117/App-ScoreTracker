package com.daniher.scoretracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LostPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_password)

        val etLostEmail = findViewById<EditText>(R.id.txtChPass)
        val btnSendPasswordReset = findViewById<Button>(R.id.btnChPassword)

        firebaseAuth = FirebaseAuth.getInstance()

        btnSendPasswordReset.setOnClickListener {
            val email = etLostEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Se ha enviado un correo de restablecimiento de contraseña a $email",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                            this,
                            "El correo electrónico $email no está registrado",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al enviar el correo de restablecimiento de contraseña: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}