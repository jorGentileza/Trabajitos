package com.trabajitos.apptrabajitos


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trabajitos.apptrabajitos.databinding.ActivityCreateProfileBinding


@Suppress("DEPRECATION")
class CreateProfile : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var firebaseFireStore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var code = 80
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()

        binding.selectPhoto.setOnClickListener {
            code = 10
            ImageController.selectPhotoFromGallery(this, code)
        }
        binding.optionalPhotoOne.setOnClickListener {
            code = 20
            ImageController.selectPhotoFromGallery(this, code)
        }
        binding.optionalPhotoTwo.setOnClickListener {
            code = 30
            ImageController.selectPhotoFromGallery(this, code)
        }
        binding.optionalPhotoThree.setOnClickListener {
            code = 40
            ImageController.selectPhotoFromGallery(this, code)
        }
        binding.optionalPhotoFour.setOnClickListener {
            code = 50
            ImageController.selectPhotoFromGallery(this, code)
        }
        binding.sendButton.setOnClickListener {
            setExperience()
            startActivity(Intent(this,HomeActivity::class.java))
            finish()

        }
    }



    /*  Esta funcion actualiza el documento de firestore del usuario en curso,
        agrega al documento los tipos de trabajo que realiza el usuario
     */
    private fun setExperience() {
        val firebaseUser = firebaseAuth.currentUser
        val email = firebaseUser!!.email.toString()
        val updateUser = firebaseFireStore.collection("users").document(email)

        if (binding.belleza.isChecked) {
            updateUser.update("beauty", true)
        }else{
            updateUser.update("beauty", false)
        }
        if(binding.construccion.isChecked){
            updateUser.update("construction", true)
        }else{
            updateUser.update("construction", false)
        }
        if(binding.educacion.isChecked) {
            updateUser.update("education", true)
        }else{
            updateUser.update("education", false)
        }
        if(binding.holisticas.isChecked) {
            updateUser.update("holistic", true)
        }else{
            updateUser.update("holistic", false)
        }
        if(binding.jardineria.isChecked) {
            updateUser.update("garden", true)
        }else{
            updateUser.update("garden", false)
        }
        if(binding.mascotas.isChecked) {
            updateUser.update("mascots", true)
        }else{
            updateUser.update("mascots", false)
        }
        if(binding.moda.isChecked) {
            updateUser.update("fashion", true)
        }else{
            updateUser.update("fashion", false)
        }
        if(binding.salud.isChecked) {
            updateUser.update("health", true)
        }else{
            updateUser.update("health", false)
        }
        if(binding.tecnologias.isChecked) {
            updateUser.update("tech", true)
        }else{
            updateUser.update("tech", false)
        }
        if(binding.other.text.toString() != "") {
            val other = binding.other.text.toString()
            updateUser.update("other", other)
        }
}

    /*  Este metodo ingresa el uri de la imagen en el imageview
        correspondiente al codigo obtenido del onClicklistener
        de cada imageview y tambien sube la foto a firebase storage
     */
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

        val firebaseUser = firebaseAuth.currentUser
        val email = firebaseUser!!.email.toString()
        val proofOne = "1"
        val proofTwo = "2"
        val proofThree = "3"
        val proofFour = "4"

    when {
        (requestCode == code && resultCode == RESULT_OK) -> {
            when (code) {
                10 -> {
                    imageUri = data!!.data
                    binding.selectPhoto.setImageURI(imageUri)
                    FirebaseStorageController().uploadProfile(data.data!!,email)
                }
                20 -> {
                    imageUri = data!!.data
                    binding.optionalPhotoOne.setImageURI(imageUri)
                    FirebaseStorageController().uploadProofPhotos(data.data!!,email,proofOne)
                }
                30 -> {
                    imageUri = data!!.data
                    binding.optionalPhotoTwo.setImageURI(imageUri)
                    FirebaseStorageController().uploadProofPhotos(data.data!!,email,proofTwo)
                }
                40 -> {
                    imageUri = data!!.data
                    binding.optionalPhotoThree.setImageURI(imageUri)
                    FirebaseStorageController().uploadProofPhotos(data.data!!,email,proofThree)

                }
                50 -> {
                    imageUri = data!!.data
                    binding.optionalPhotoFour.setImageURI(imageUri)
                    FirebaseStorageController().uploadProofPhotos(data.data!!,email,proofFour)

                }
            }

        }

    }

}

}