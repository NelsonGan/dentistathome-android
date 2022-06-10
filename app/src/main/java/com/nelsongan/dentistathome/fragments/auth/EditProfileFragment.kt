package com.nelsongan.dentistathome.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentEditProfileBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user
        loadUser()
        populateUser()

        // Add event listeners
        addEventListeners()
    }

    private fun addEventListeners() {
        binding.buttonUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    private fun populateUser() {
        // Populate the fields
        with (binding) {
            inputEmail.setText(user.email)
            inputRole.setText(user.role)
            inputName.setText(user.name)
            inputGender.setText(user.gender)
            inputAddressLine1.setText(user.addressLine1)
            inputAddressLine2.setText(user.addressLine2)
            inputPhoneNumber.setText(user.phoneNumber)
        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val addressLine1Validation: Boolean = binding.inputAddressLine1.text.toString().trim().isNotEmpty()
        val addressLine2Validation: Boolean = binding.inputAddressLine2.text.toString().trim().isNotEmpty()
        val phoneNumberValidation: Boolean = binding.inputPhoneNumber.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!addressLine1Validation) {
            binding.inputAddressLine1.error = blankMessage
        }

        if (!addressLine2Validation) {
            binding.inputAddressLine2.error = blankMessage
        }

        if (!phoneNumberValidation) {
            binding.inputPhoneNumber.error = blankMessage
        }

        if (!addressLine1Validation || !addressLine2Validation || !phoneNumberValidation) {
            Toast.makeText(activity,"Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateProfile() {
        if (validateInputFields()) {
            // Get input
            val addressLine1: String = binding.inputAddressLine1.text.toString().trim()
            val addressLine2: String = binding.inputAddressLine2.text.toString().trim()
            val phoneNumber: String = binding.inputPhoneNumber.text.toString().trim()

            // Perform update
            val userHashMap = hashMapOf(
                "addressLine1" to addressLine1,
                "addressLine2" to addressLine2,
                "phoneNumber" to phoneNumber,
            )

            FirebaseUtils().firestore
                .collection("users")
                .document(user.userId!!)
                .update(userHashMap as Map<String, Any>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Update success!", Toast.LENGTH_SHORT).show()

                        // Go to profile home
                        view?.findNavController()?.navigate(R.id.action_editProfileFragment_to_profileFragment)
                    } else {
                        // Error handling
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}