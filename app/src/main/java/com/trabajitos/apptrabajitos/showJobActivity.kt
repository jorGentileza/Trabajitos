package com.trabajitos.apptrabajitos

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.trabajitos.apptrabajitos.databinding.ActivityShowJobBinding
import java.io.File

class showJobActivity : AppCompatActivity() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var binding: ActivityShowJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        firebaseFirestore = FirebaseFirestore.getInstance()
        loadInfo()

        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.sendButton.setOnClickListener {
            val email = binding.email.text.toString()
            val subject = "Hola! me interesa tu trabajito -${binding.titulo.text}"
            val intentEmail = Intent(Intent.ACTION_SEND, Uri.parse(email))
            intentEmail.type = "plain/text"
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, subject)
            intentEmail.putExtra(Intent.EXTRA_TEXT, "Hola, me interesa el trabajito \n " +
                    "Titulado ${binding.titulo.text} \n ${binding.descripcion.text}")
            intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            startActivity(Intent.createChooser(intentEmail, "Elige cliente de correo"))
        }

    }

    private fun loadInfo() {

        val id = intent.getStringExtra("id").toString()

        firebaseFirestore.collection("posts").document(id)
            .get()
            .addOnSuccessListener { document ->
                val descripcion: String? = document.getString("description").toString()
                val email: String = document.getString("email").toString()
                val pay: String? = document.getString("pay")
                val title: String? = document.getString("title").toString()

                binding.titulo.text = title
                binding.paga.text = "Paga: $$pay"
                binding.descripcion.text = descripcion
                binding.email.text = email

                firebaseFirestore.collection("users").document(email)
                    .get()
                    .addOnSuccessListener { document ->
                        val nombre = document.getString("first")
                        val apellido = document.getString("last")
                        val beauty: Boolean? = document.getBoolean("beauty")
                        val construction: Boolean? = document.getBoolean("construction")
                        val education: Boolean? = document.getBoolean("education")
                        val fashion: Boolean? = document.getBoolean("fashion")
                        val garden: Boolean? = document.getBoolean("garden")
                        val health: Boolean? = document.getBoolean("health")
                        val holistic: Boolean? = document.getBoolean("holistic")
                        val mascots: Boolean? = document.getBoolean("mascots")
                        val tech: Boolean? = document.getBoolean("tech")

                        if (beauty == true) {
                            binding.badgeOneImageView.isVisible = true
                        }
                        if (construction == true) {
                            binding.badgeTwoImageView.isVisible = true
                        }
                        if (education == true) {
                            binding.badgeThreeImageView.isVisible = true
                        }
                        if (fashion == true) {
                            binding.badgeFourImageView.isVisible = true
                        }
                        if (garden == true) {
                            binding.badgeFiveImageView.isVisible = true
                        }
                        if (health == true) {
                            binding.badgeSixImageView.isVisible = true
                        }
                        if (holistic == true) {
                            binding.badgeSevenImageView.isVisible = true
                        }
                        if (mascots == true) {
                            binding.badgeEightImageView.isVisible = true
                        }
                        if (tech == true) {
                            binding.badgeNineImageView.isVisible = true
                        }

                        val completo = "$nombre $apellido"
                        binding.name.text = completo
                        val localFile = File.createTempFile("tempImage", "jpg")
                        val storageProfile =
                            FirebaseStorage.getInstance().reference.child("users/profilePicture/$email")
                        storageProfile.getFile(localFile).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            binding.profilepic.setImageBitmap(bitmap)
                        }.addOnFailureListener {
                            Toast.makeText(this, "puchis no funco", Toast.LENGTH_LONG).show()
                        }

                    }
            }

    }

}