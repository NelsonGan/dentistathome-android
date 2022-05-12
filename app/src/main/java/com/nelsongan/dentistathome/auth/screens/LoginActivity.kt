package com.nelsongan.dentistathome.auth.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.databinding.ActivityLoginBinding
import com.nelsongan.dentistathome.misc.screens.MenuActivity
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        authCheck()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {

    }

    private fun addEventListeners() {
        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            if (validateInputFields()) {
                // Get input
                val email: String = binding.inputEmail.text.toString().trim()
                val password: String = binding.inputPassword.text.toString()

                // Perform login
                FirebaseUtils().auth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser? = task.result!!.user

                            // Check if user completed registration
                            FirebaseUtils().firestore
                                .collection("users")
                                .document(firebaseUser!!.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Go to menu if already complete registration
                                        val intent = Intent(this, MenuActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Create a User object
                                        val user = User(firebaseUser.uid, firebaseUser.email!!)

                                        // Navigate to CompleteRegistrationActivity with data if hasn't complete registration
                                        Toast.makeText(this, "Please complete your registration details", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, CompleteRegistrationActivity::class.java)
                                        intent.putExtra("USER", user);
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            // Error handling
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun authCheck() {
        // Get current user
        val firebaseUser: FirebaseUser? = FirebaseUtils().auth.currentUser

        // If signed in
        if (firebaseUser != null) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val emailValidation: Boolean = binding.inputEmail.text.toString().trim().isNotEmpty()
        val passwordValidation: Boolean = binding.inputPassword.text.toString().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!emailValidation) {
            binding.inputEmail.error = blankMessage
        }

        if (!passwordValidation) {
            binding.inputPassword.error = blankMessage
        }

        if (!emailValidation || !passwordValidation) {
            Toast.makeText(this,"Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}