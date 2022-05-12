package com.nelsongan.dentistathome.auth.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.nelsongan.dentistathome.databinding.ActivityRegisterBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {

    }

    private fun addEventListeners() {
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonRegister.setOnClickListener {
            if (validateInputFields()) {
                // Get input
                val email: String = binding.inputEmail.text.toString().trim()
                val password: String = binding.inputPassword.text.toString()

                // Perform registration
                FirebaseUtils().auth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Successful Registration
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            // Create a User object
                            val user = User(firebaseUser.uid, firebaseUser.email!!)

                            // Navigate to CompleteRegistrationActivity with data
                            val intent = Intent(this, CompleteRegistrationActivity::class.java)
                            intent.putExtra("USER", user);
                            startActivity(intent)
                            finish()
                        } else {
                            // Error handling
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val emailValidation: Boolean = binding.inputEmail.text.toString().trim().isNotEmpty()
        val passwordValidation: Boolean = binding.inputPassword.text.toString().isNotEmpty()
        val confirmPasswordValidation: Boolean =
            binding.inputConfirmPassword.text.toString().isNotEmpty()
        val passwordSameValidation: Boolean =
            binding.inputPassword.text.toString() == binding.inputConfirmPassword.text.toString()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"
        val passwordSameMessage: String = "Password and confirm password must be same"

        if (!emailValidation) {
            binding.inputEmail.error = blankMessage
        }

        if (!passwordValidation) {
            binding.inputPassword.error = blankMessage
        }

        if (!confirmPasswordValidation) {
            binding.inputConfirmPassword.error = blankMessage
        }

        if (!passwordSameValidation) {
            binding.inputPassword.error = passwordSameMessage
            binding.inputConfirmPassword.error = passwordSameMessage
        }

        if (!emailValidation || !passwordValidation || !confirmPasswordValidation || !passwordSameValidation) {
            Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}