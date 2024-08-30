package com.gabrieltsujimoto.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gabrieltsujimoto.whatsappclone.databinding.ActivityLoginScreenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginScreenActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginScreenBinding.inflate(layoutInflater)
    }

    private val authentication by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initListeneters()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initListeneters() {
        binding.txtSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEditEmail.text.toString()
            val password = binding.txtEditPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authentication.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { res ->
                        Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                                .putExtra("uid", res.user?.uid)
                        )
                    }
                    .addOnFailureListener { err ->
                        try {
                            throw err
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Snackbar.make(
                                this,
                                binding.root,
                                "E-mail ou senha incorretos",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}