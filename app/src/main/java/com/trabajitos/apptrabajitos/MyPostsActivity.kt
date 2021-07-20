package com.trabajitos.apptrabajitos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trabajitos.apptrabajitos.databinding.ActivityMyPostsBinding

class MyPostsActivity : AppCompatActivity() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMyPostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()

    }


    private fun setUp() {
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val email = firebaseUser!!.email.toString()
        var contador = 0
        firebaseFirestore = FirebaseFirestore.getInstance()
        // buscamos la coleccion posts y recorremos todos los documentos.
        val posts = firebaseFirestore.collection("jobs").document(email)
        posts.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    when (contador) {
                        0 -> {
                            binding.firstTitle.text = document.getString("title")
                            binding.firstPost.isVisible = true
                        }
                        1 -> {
                            binding.secondTitle.text = document.getString("title")
                            binding.secondPost.isVisible = true
                        }
                        2 -> {
                            binding.thirdTitle.text = document.getString("title")
                            binding.thirdPost.isVisible = true
                        }
                    }
                    contador++
                }
            }.addOnFailureListener {
                Toast.makeText(this, "no funco", Toast.LENGTH_LONG).show()
            }

        binding.firstMessage.setOnClickListener{
            
        }

        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}