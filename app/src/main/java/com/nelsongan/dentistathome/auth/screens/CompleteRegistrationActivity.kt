package com.nelsongan.dentistathome.auth.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.databinding.ActivityCompleteRegistrationBinding
import com.nelsongan.dentistathome.misc.screens.MenuActivity
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class CompleteRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteRegistrationBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readIntents()
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun readIntents() {
        user = intent.getSerializableExtra("USER") as User
    }

    private fun runViewBinding() {
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {
        // Populate spinner
        val genders = resources.getStringArray(R.array.Genders)
        val genderArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, genders
        )
        binding.inputGender.adapter = genderArrayAdapter

        val roles = resources.getStringArray(R.array.Roles)
        val roleArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, roles
        )
        binding.inputRole.adapter = roleArrayAdapter

        // Populate fields
        binding.inputEmail.setText(user.email)
    }

    private fun addEventListeners() {
        binding.buttonRegister.setOnClickListener {
            if (validateInputFields()) {
                // Get input
                val email: String = binding.inputEmail.text.toString().trim()
                val role: String = binding.inputRole.selectedItem.toString().trim()
                val name: String = binding.inputName.text.toString().trim()
                val gender: String = binding.inputGender.selectedItem.toString().trim()
                val addressLine1: String = binding.inputAddressLine1.text.toString().trim()
                val addressLine2: String = binding.inputAddressLine2.text.toString().trim()

                // Perform registration
                val userHashMap = hashMapOf(
                    "email" to email,
                    "name" to name,
                    "gender" to gender,
                    "role" to role,
                    "addressLine1" to addressLine1,
                    "addressLine2" to addressLine2,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                FirebaseUtils().firestore
                    .collection("users")
                    .document(user.userId)
                    .set(userHashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration success!", Toast.LENGTH_SHORT).show()

                            // Go to menu
                            val intent = Intent(this, MenuActivity::class.java)
                            startActivity(intent)
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
        val nameValidation: Boolean = binding.inputName.text.toString().trim().isNotEmpty()
        val addressLine1Validation: Boolean =
            binding.inputAddressLine1.text.toString().trim().isNotEmpty()
        val addressLine2Validation: Boolean =
            binding.inputAddressLine2.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!nameValidation) {
            binding.inputName.error = blankMessage
        }

        if (!addressLine1Validation) {
            binding.inputAddressLine1.error = blankMessage
        }

        if (!addressLine2Validation) {
            binding.inputAddressLine2.error = blankMessage
        }


        if (!nameValidation || !addressLine1Validation || !addressLine2Validation) {
            Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}