package com.nelsongan.dentistathome.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentCompleteRegistrationBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class CompleteRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentCompleteRegistrationBinding
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompleteRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUser()
        addEventListeners()
        populateFields()
    }

    private fun getUser() {
        currentUser = FirebaseUtils().auth.currentUser
    }

    private fun populateFields() {
        // Populate fields
        binding.inputEmail.setText(currentUser!!.email)

        // Populate Spinners
        activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Genders,
                android.R.layout.simple_spinner_item
            )
                .also { adapter ->
                    binding.inputGender.adapter = adapter
                }
        }

        activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Roles,
                android.R.layout.simple_spinner_item
            )
                .also { adapter ->
                    binding.inputRole.adapter = adapter
                }
        }
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
                val phoneNumber: String = binding.inputPhoneNumber.text.toString().trim()

                // Perform registration
                val userHashMap = hashMapOf(
                    "email" to email,
                    "name" to name,
                    "gender" to gender,
                    "role" to role,
                    "addressLine1" to addressLine1,
                    "addressLine2" to addressLine2,
                    "phoneNumber" to phoneNumber,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                FirebaseUtils().firestore
                    .collection("users")
                    .document(currentUser!!.uid)
                    .set(userHashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            FirebaseUtils().firestore
                                .collection("users")
                                .document(currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Grab the user object
                                        val user = document.toObject<User>()!!
                                        user.userId = document.id

                                        // Save user to shared preferences
                                        activity?.let { UserConstants.saveUser(it, user) }

                                        // Go to home
                                        view?.findNavController()
                                            ?.navigate(R.id.action_completeRegistrationFragment_to_homeFragment)
                                    }
                                }

                        } else {
                            // Error handling
                            Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
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
        val phoneNumberValidation: Boolean =
            binding.inputPhoneNumber.text.toString().trim().isNotEmpty()

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

        if (!phoneNumberValidation) {
            binding.inputPhoneNumber.error = blankMessage
        }

        if (!nameValidation || !addressLine1Validation || !addressLine2Validation || !phoneNumberValidation) {
            Toast.makeText(activity, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}