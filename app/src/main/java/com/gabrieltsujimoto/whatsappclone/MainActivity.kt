package com.gabrieltsujimoto.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gabrieltsujimoto.whatsappclone.databinding.ActivityMainBinding
import com.gabrieltsujimoto.whatsappclone.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    /*private lateinit var name: String
    private lateinit var photo: String
    private lateinit var mail: String*/

    private lateinit var user: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val userId = intent.getStringExtra("uid")
        if (userId != null) {
            getUserData(userId)
//            putText()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun putText(user: Usuario) {
        val fstLetter = user.name.first()

        binding.tbMain.button.text = "$fstLetter"
        binding.txtName.text = user.name
        binding.txtMail.text = user.mail
        binding.txtPhoto.text = user.uid
    }

    private fun getUserData(id: String){
        database.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { onSuccess ->
                val databaseData = onSuccess.data
                user = Usuario(
                    databaseData?.get("uid").toString(),
                    databaseData?.get("name").toString(),
                    databaseData?.get("photo").toString(),
                    databaseData?.get("mail").toString()
                )
                Log.i("info_database_connect", "Dados do usuário: $user ")
                putText(user)

            }
            .addOnFailureListener { onFailure ->
                Log.i("info_database_connect", "${onFailure.printStackTrace()}")
            }
    }
}