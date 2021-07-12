package com.trabajitos.apptrabajitos


import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trabajitos.apptrabajitos.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFireStore: FirebaseFirestore

    private var email = ""
    private var password = ""
    private var name = ""
    private var lastName = ""
    private var phone = ""
    private var confirmPassword = ""
    private var birthDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
    }

    private fun setUp() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setMessage("Creando cuenta")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()

        binding.sUpButton.setOnClickListener {
            validateData()
        }
        binding.closeButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        binding.birthEditTextDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    /* metodo que valida los campos ingresados, deben cumplir requisitos
        para ser creados como usuarios, si la validacion es correcta
        llama al metodo que crea la autenticacion con email y pass
    */
    private fun validateData() {
        email = binding.emailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()
        name = binding.nameEditText.text.toString().trim()
        lastName = binding.lastNameEditText.text.toString().trim()
        phone = binding.phoneEditText.text.toString().trim()
        confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        birthDate = binding.birthEditTextDate.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "formato de correo incorrecto"
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "ingrese contraseña"
        } else if (password.length < 6) {
            binding.passwordEditText.error = "el largo debe ser minimo 6 caracteres"
        } else if (TextUtils.isEmpty(name)) {
            binding.nameEditText.error = "ingrese nombre"
        } else if (TextUtils.isEmpty(lastName)) {
            binding.lastNameEditText.error = "ingrese apellido"
        } else if (TextUtils.isEmpty(phone)) {
            binding.phoneEditText.error = "ingrese telefono"
        } else if (TextUtils.isEmpty(birthDate)) {
            binding.birthEditTextDate.error = "ingrese fecha de nacimiento"
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordEditText.error = "debe confirmar contraseña"
        } else if (password != confirmPassword) {
            binding.passwordEditText.error = "las contraseñas deben coincidir"
            binding.confirmPasswordEditText.error = "las contraseñas deben coincidir"
        } else {
            authSignUp()
        }
    }

    /* este metodo se encarga de crear la "firebase auth",
        si el registro es exitoso se llama al metodo que creara
        el documento de firestore, de no funcionar el registro
        lanzara un toast con una excepcion donde se indica el motivo
     */
    private fun authSignUp() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                fireStoreSignUp()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "error ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /* este metodo crea un hashmap con los datos de registro, que sera pasado
        como parametro para la creacion del documento en firestore
     */
    private fun fireStoreSignUp() {
        val user = hashMapOf(
            "first" to name,
            "last" to lastName,
            "birthDate" to birthDate,
            "email" to email,
            "phone" to phone,
            "password" to password
        )
        firebaseFireStore.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Bienvenido $name", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CreateProfile::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "error ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // este metodo instancia el controlador del calendario y lo despliega en pantalla
    private fun showDatePickerDialog() {
        val datePicker =
            DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "Date Picker")
    }

    // este metodo escribe la fecha seleccionada en el editText
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val birthEditText: EditText = findViewById(R.id.birthEditTextDate)
        birthEditText.setText("$day / $month / $year")
    }

}