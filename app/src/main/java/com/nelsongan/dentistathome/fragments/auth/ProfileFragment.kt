package com.nelsongan.dentistathome.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentProfileBinding
import com.nelsongan.dentistathome.fragments.inspection.InspectionReviewFragmentDirections
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils
import java.text.SimpleDateFormat

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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
        with (binding) {
            // Edit profile button
            btnEditProfile.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_profileFragment_to_editProfileFragment)
            }

            // Logout button
            btnLogout.setOnClickListener {
                FirebaseUtils().auth.signOut().also {
                    // Clear user to shared preferences
                    activity?.let { UserConstants.clearUserFromSharedPreference(it) }

                    // Navigate to login page
                    view?.findNavController()?.navigate(R.id.action_global_loginFragment)
                }
            }

            // Add event listener to point to own profile
            btnViewProfile.setOnClickListener { view ->
                val action = ProfileFragmentDirections.actionProfileFragmentToUserShowFragment(user.userId!!)
                view.findNavController().navigate(action)
            }
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
            txtName.text = user.name

            // Set join date
            if (user.createdAt != null) {
                val formattedDate: String = "Joined on " + SimpleDateFormat("MMMM yyyy").format(user.createdAt!!)
                txtCreatedAt.text = formattedDate
            }
        }
    }
}