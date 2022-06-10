package com.nelsongan.dentistathome.fragments.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentLoginBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addEventListeners()
    }

    private fun addEventListeners() {
        with (binding) {
            buttonRegister.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonLogin.setOnClickListener {
                loginUser()
            }
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
            Toast.makeText(activity,"Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun loginUser() {
        if (validateInputFields()) {
            // Get input
            val email: String = binding.inputEmail.text.toString().trim()
            val password: String = binding.inputPassword.text.toString()

            // Perform login
            FirebaseUtils().auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser? = task.result!!.user
                        // Check if user completed registration
                        FirebaseUtils().firestore
                            .collection("users")
                            .document(currentUser!!.uid)
                            .get()
                            .addOnSuccessListener { document->
                                if (document.exists()) {

                                    // Grab the user object
                                    user = document.toObject<User>()!!
                                    user.userId = document.id

                                    // Save user to shared preferences
                                    activity?.let { UserConstants.saveUser(it, user) }

                                    // Go to home
                                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
                                } else {
                                    // Go to complete registration
                                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_completeRegistrationFragment)
                                    Toast.makeText(activity, "Please complete your registration details", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        // Error handling
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }
}