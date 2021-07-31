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
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        showPosts()
        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun showPosts() {
        val firebaseUser = firebaseAuth.currentUser
        val email = firebaseUser!!.email.toString()
        var contador = 0

        // buscamos la coleccion posts y recorremos todos los documentos.
        firebaseFirestore.collection("posts")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    when (contador) {
                        0 -> {
                            binding.firstTitle.text = document.getString("title")
                            binding.firstPost.isVisible = true
                            val id = document.id
                            binding.firstDelete.setOnClickListener {
                                firebaseFirestore.collection("posts").document(id)
                                    .delete()
                                    .addOnSuccessListener {
                                        binding.firstPost.isVisible = false
                                        Toast.makeText(
                                            this,
                                            "Trabajito Eliminado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        }
                        1 -> {
                            binding.secondTitle.text = document.getString("title")
                            binding.secondPost.isVisible = true
                            val id = document.id
                            binding.secondDelete.setOnClickListener {
                                firebaseFirestore.collection("posts").document(id)
                                    .delete()
                                    .addOnSuccessListener {
                                        binding.secondPost.isVisible = false
                                        Toast.makeText(
                                            this,
                                            "Trabajito Eliminado",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }

                        }
                        2 -> {
                            binding.thirdTitle.text = document.getString("title")
                            binding.thirdPost.isVisible = true
                            val id = document.id
                            binding.thirDelete.setOnClickListener {
                                firebaseFirestore.collection("posts").document(id)
                                    .delete()
                                    .addOnSuccessListener {
                                        binding.thirdPost.isVisible = false
                                        Toast.makeText(
                                            this,
                                            "Trabajito Eliminado",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }

                        }
                    }
                    contador++
                }

            }.addOnFailureListener {
                Toast.makeText(this, "no funco", Toast.LENGTH_LONG).show()
            }
    }
}