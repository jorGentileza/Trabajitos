package com.trabajitos.apptrabajitos

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.trabajitos.apptrabajitos.databinding.ActivityMyProfileBinding
import java.io.File

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        validateUser()
        binding.closeButton.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
        binding.deleteAccountButton.setOnClickListener {
            deleteAccount()
        }
    }

    private fun validateUser() {

        val email = firebaseAuth.currentUser!!.email.toString()

        firebaseFirestore.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name: String? = document.getString("first")
                    val lastName: String? = document.getString("last")
                    val beauty: Boolean? = document.getBoolean("beauty")
                    val construction: Boolean? = document.getBoolean("construction")
                    val education: Boolean? = document.getBoolean("education")
                    val fashion: Boolean? = document.getBoolean("fashion")
                    val garden: Boolean? = document.getBoolean("garden")
                    val health: Boolean? = document.getBoolean("health")
                    val holistic: Boolean? = document.getBoolean("holistic")
                    val mascots: Boolean? = document.getBoolean("mascots")
                    val tech: Boolean? = document.getBoolean("tech")
                    val other: String? = document.getString("other")

                    binding.nameEditText.text = "$name  $lastName"

                    if(beauty == true){
                        binding.badgeOneImageView.isVisible = true
                    }
                    if(construction == true){
                        binding.badgeTwoImageView.isVisible = true
                    }
                    if(education == true){
                        binding.badgeThreeImageView.isVisible = true
                    }
                    if(fashion == true){
                        binding.badgeFourImageView.isVisible = true
                    }
                    if(garden == true){
                        binding.badgeFiveImageView.isVisible = true
                    }
                    if(health == true){
                        binding.badgeSixImageView.isVisible = true
                    }
                    if(holistic == true){
                        binding.badgeSevenImageView.isVisible = true
                    }
                    if(mascots == true){
                        binding.badgeEightImageView.isVisible = true
                    }
                    if(tech == true){
                        binding.badgeNineImageView.isVisible = true
                    }
                } else {
                    Toast.makeText(this,"error ",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                Log.e(TAG,"error deleting photos because ${it.printStackTrace()}")
            }

        val localFile = File.createTempFile("tempImage","jpg")


        val storageProfile = FirebaseStorage.getInstance().reference.child("users/profilePicture/$email")
        storageProfile.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profileImageView.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"puchis no funco",Toast.LENGTH_LONG).show()
        }
        val storageProofOne = FirebaseStorage.getInstance().reference.child("users/proofPhotos/$email/1")
        storageProofOne.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.proofOneImageView.setImageBitmap(bitmap)
            binding.proofOneImageView.isVisible = true
        }.addOnFailureListener{
        }
        val storageProofTwo = FirebaseStorage.getInstance().reference.child("users/proofPhotos/$email/2")
        storageProofTwo.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.proofTwoImageView.setImageBitmap(bitmap)
            binding.proofTwoImageView.isVisible = true
        }.addOnFailureListener{
        }
        val storageProofThree = FirebaseStorage.getInstance().reference.child("users/proofPhotos/$email/3")
        storageProofThree.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.proofThreeImageView.setImageBitmap(bitmap)
            binding.proofThreeImageView.isVisible = true
        }.addOnFailureListener{
        }
        val storageProofFour = FirebaseStorage.getInstance().reference.child("users/proofPhotos/$email/4")
        storageProofFour.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.proofFourImageView.setImageBitmap(bitmap)
            binding.proofFourImageView.isVisible = true
        }.addOnFailureListener{
        }

    }
        /*  esta funcion sirve para borrar un documento de firestore,
    obtiene el correo del usuario de la sesion en curso,
    luego se convoca al metodo de firebase para eliminar documento

 */
        private fun deleteAccount() {

            val firebaseUser = firebaseAuth.currentUser
            val email = firebaseUser!!.email.toString()

            firebaseFirestore.collection("users").document(email)
                .delete()
                .addOnSuccessListener {
                    firebaseUser.delete()
                    FirebaseAuth.getInstance().signOut()
                    FirebaseStorageController().deleteAccount(email)
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
        }


}