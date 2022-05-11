package com.nelsongan.dentistathome.auth.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {
        supportActionBar?.hide()
    }

    private fun addEventListeners() {
        binding.buttonLogin.setOnClickListener {
            if (validateInputFields()) {
                // Perform login

            }
        }

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val emailValidation: Boolean = binding.inputEmail.text.isNotEmpty()
        val passwordValidation: Boolean = binding.inputPassword.text.isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!emailValidation) {
            binding.inputEmail.error = blankMessage
        }

        if (!passwordValidation) {
            binding.inputPassword.error = blankMessage
        }

        if (!emailValidation || !passwordValidation) {
            Toast.makeText(applicationContext,"Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}