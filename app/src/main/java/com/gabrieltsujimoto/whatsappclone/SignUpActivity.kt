package com.gabrieltsujimoto.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gabrieltsujimoto.whatsappclone.databinding.ActivitySignUpBinding
import com.gabrieltsujimoto.whatsappclone.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

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
            name = binding.inputEditName.text.toString()
            email = binding.inputEditMail.text.toString()
            password = binding.inputEditPassword.text.toString()
            validateField()
            createNewUser(name, email, password)
        }
    }

    private fun validateField(): Boolean {
        if (name.isNotEmpty()) {
            binding.txtInputLayoutName.error = null
            if (email.isNotEmpty()) {
                binding.txtInputLayoutMail.error = null
                if (password.isNotEmpty()) {
                    binding.txtInputLayoutPass.error = null
                } else {
                    binding.txtInputLayoutPass.error = "Digite sua senha"
                    return false
                }
            } else {
                binding.txtInputLayoutMail.error = "Digite seu e-mail"
                return false
            }
        } else {
            binding.txtInputLayoutName.error = "Digite seu nome"
            return false
        }
        return true
    }

    private fun createNewDatabaseUser(user: Usuario){
        database.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Cadastro de ${user.name} realizado",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                        .putExtra("uid", user.uid)
                )
            }.addOnFailureListener {
                Log.i("info_database_connect", "${it.printStackTrace()}")
            }
    }

    private fun createNewUser(name: String, email: String, password: String) {
        val fieldComplete: Boolean = validateField()
        if (fieldComplete) {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid
                    if (userId != null) {
                        authentication.signInWithEmailAndPassword(email, password)
                        val userData = Usuario(
                            userId,
                            name,
                            "",
                            email
                        )
                        createNewDatabaseUser(userData)
                    }
                }.addOnFailureListener { err ->
                    try {
                        throw err
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(
                            this,
                            "Senha fraca: digite com letras, números e caracteres especiais.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "E-mail já cadastrado", Toast.LENGTH_SHORT).show()
                    }
                    Log.i(
                        "info_database_connect",
                        "não foi possível cadastrar usuário: ${err.printStackTrace()}"
                    )
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