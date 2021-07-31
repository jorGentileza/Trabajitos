package com.trabajitos.apptrabajitos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()
        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.sendButton.setOnClickListener {
            val category = spinner.selectedItem.toString()
            validateData(category)
        }
    }

    private fun validateData(category: String){
        email = firebaseAuth.currentUser!!.email.toString()
        title = binding.titleInput.text.toString()
        descrip = binding.descriptionInput.text.toString()
        pay = binding.payInput.text.toString()

        createJob(category)
    }

    /*
        Esta funcion crea un hasmap que sera pasado como parametro para agregarlo a la coleccion
        de Firebase Firestore
     */
    private fun createJob(category: String){
        var longitude = intent.getStringExtra("longitud")
        var latitude = intent.getStringExtra("latitud")
        val job = hashMapOf(
            "email" to email,
            "title" to title,
            "description" to descrip,
            "pay" to pay,
            "lat" to latitude,
            "long" to longitude,
            "category" to category
        )

      firebaseFireStore.collection("posts").document()
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