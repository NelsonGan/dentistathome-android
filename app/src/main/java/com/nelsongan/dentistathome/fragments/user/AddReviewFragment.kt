package com.nelsongan.dentistathome.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentAddReviewBinding
import com.nelsongan.dentistathome.databinding.FragmentUserShowBinding
import com.nelsongan.dentistathome.fragments.inspection.InspectionShowFragmentDirections
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class AddReviewFragment : Fragment() {
    private lateinit var binding: FragmentAddReviewBinding
    private lateinit var user: User
    private lateinit var profileUser: User

    // Get argument (user id)
    val args: UserShowFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user
        loadUser()
        populateUser()

        // Load profile
        loadProfile()

        // Add event listeners
        addEventListeners()
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    private fun populateUser() {
        with (binding) {
            // Set name
            txtName.text = user.name
        }
    }

    private fun loadProfile() {
        val profileUserId: String = args.userId

        FirebaseUtils().firestore
            .collection("users")
            .document(profileUserId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Set the user object
                if (documentSnapshot.exists()) {
                    profileUser = documentSnapshot.toObject<User>()!!
                    profileUser.userId = documentSnapshot.id
                }

                // Populate profile
                populateProfile()
            }
    }

    private fun populateProfile() {
        with (binding) {
            // Set title
            val titleString: String = "Reviewing " + profileUser.name
            title.text = titleString

            // Make cancel button
            btnCancel.setOnClickListener { view ->
                val action = AddReviewFragmentDirections.actionAddReviewFragmentToUserShowFragment(profileUser.userId!!)
                view.findNavController().navigate(action)
            }
        }
    }

    private fun addEventListeners() {
        with (binding) {
            btnSubmit.setOnClickListener {
                addReview()
            }
        }
    }

    private fun validateInputFields() : Boolean {
        val reviewContentValidation: Boolean = binding.inputReviewContent.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!reviewContentValidation) {
            binding.inputReviewContent.error = blankMessage
        }

        if (!reviewContentValidation) {
            Toast.makeText(activity, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun addReview() {
        if (validateInputFields()) {
            // Get input
            val reviewContent: String = binding.inputReviewContent.text.toString().trim()
            val recommended: Boolean = !(binding.cbxNotRecommend.isChecked)

            val reviewHashMap = hashMapOf(
                "userId" to profileUser.userId,
                "reviewerId" to user.userId,
                "reviewerName" to user.name,
                "reviewContent" to reviewContent,
                "recommended" to recommended,
                "createdAt" to FieldValue.serverTimestamp()
            )

            FirebaseUtils().firestore
                .collection("reviews")
                .add(reviewHashMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Go back to user profile
                        Toast.makeText(activity, "You've successfully reviewed " + profileUser.name + "!", Toast.LENGTH_SHORT).show()
                        val action = AddReviewFragmentDirections.actionAddReviewFragmentToUserShowFragment(profileUser.userId!!)
                        view?.findNavController()?.navigate(action)
                    } else {
                        // Error handling
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}