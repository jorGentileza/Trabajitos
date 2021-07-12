package com.trabajitos.apptrabajitos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trabajitos.apptrabajitos.databinding.ActivityPostJobBinding


class PostJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFireStore: FirebaseFirestore
    var email: String = ""
    var title: String = ""
    var descrip: String = ""
    var pay: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()
        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.sendButton.setOnClickListener {
            validateData()
        }



    }

    private fun validateData(){
        email = firebaseAuth.currentUser!!.email.toString()
        title = binding.titleInput.text.toString()
        descrip = binding.descriptionInput.text.toString()
        pay = binding.payInput.text.toString()

        createJob()
    }

    private fun createJob(){

        val job = hashMapOf(
            "email" to email,
            "title" to title,
            "description" to descrip,
            "pay" to pay
        )

        val jobs = firebaseFireStore.collection("jobs").document(email)
            jobs.collection("posts").document(title)
                .set(job)
            .addOnSuccessListener {
               Toast.makeText(this,"Trabajo ingresado",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "error ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}