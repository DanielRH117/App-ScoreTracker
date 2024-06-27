package com.daniher.scoretracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AdminLeague : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_league)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnLostPass = findViewById<TextView>(R.id.btnLostPass)
        val txtEmail = findViewById<TextView>(R.id.et_User)
        val txtPass = findViewById<TextView>(R.id.et_Pass)
        firebaseAuth = Firebase.auth
        btnLostPass.setOnClickListener { lostPass() }
        btnRegister.setOnClickListener { registerUser() }
        btnLogin.setOnClickListener {
            logInAdmin(
                txtEmail.text.toString(),
                txtPass.text.toString()
            )
        }
    }

    private fun logInAdmin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && !user.isEmailVerified) {
                        Toast.makeText(
                            this,
                            "Por favor, verifica tu correo electrónico para continuar",
                            Toast.LENGTH_SHORT
                        ).show()
                        firebaseAuth.signOut()
                    } else {
                        val intent = Intent(this, AdminLogin::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error al iniciar sesión: Usuario o contraseña incorrectos.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun registerUser() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    private fun lostPass() {
        val intent = Intent(this, LostPassword::class.java)
        startActivity(intent)
    }
}