package com.trabajitos.apptrabajitos

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.trabajitos.apptrabajitos.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAuthBinding
    private lateinit var progressDialog:ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp(){
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setMessage("Logeado")
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.sUpButton.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
        binding.logInButton.setOnClickListener{
            validateData()
        }
    }

    /* este metodo valida que el correo tenga formato correcto
        y que el campo contraseña no esté vacío,
        si la validación es correcta llama al metodo de registro
     */
    private fun validateData() {
        email = binding.emailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEditText.error = "Formato de correo incorrecto"
        }
        else if(TextUtils.isEmpty(password)){
            binding.passwordEditText.error = "Porfavor ingrese contraseña"
        }else{
            firebaseLogin()
        }
    }

    /* este metodo se encarga de logear en la "firenase auth",
        si el logueo es exitoso despliega un toast dando la bienvenida
        y lanzando la actividad, en caso contrario, de no funcionar el registro
        lanzara un toast con una excepcion donde se indica el motivo
     */
    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
            progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Bienvenido $email",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()

        }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"Ingreso fallido debido a error ${e.message}",Toast.LENGTH_LONG).show()
            }
    }

    /* este metodo se lanza al inicio de la app, y comprueba si
        existe un usuario logeado, en el caso que exista
        se lanza actividad home
     */
    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }


}