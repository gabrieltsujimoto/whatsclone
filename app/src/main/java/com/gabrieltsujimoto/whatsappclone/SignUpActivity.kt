package com.gabrieltsujimoto.whatsappclone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gabrieltsujimoto.whatsappclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private val authentication by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initToolBar()
        initListeners()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initListeners() {
        binding.btnSignUp.setOnClickListener {
            val name = binding.inputEditName.text.toString()
            val email = binding.inputEditMail.text.toString()
            val password = binding.inputEditPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authentication.createUserWithEmailAndPassword(email, password)

                    .addOnSuccessListener {
                        Toast.makeText(this, "Cadastro realizado", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Log.i("info_database_connect", "${it.printStackTrace()}")
                    }
                database.collection("users")
                    .document("${authentication.currentUser?.uid}")
                    .set(
                        listOf(
                            "name" to name,
                            "email" to email,
                            "password" to password,
                            "uid" to authentication.currentUser?.uid
                        )
                    )
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initToolBar() {
        val toolbar = binding.includeToolBar.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}