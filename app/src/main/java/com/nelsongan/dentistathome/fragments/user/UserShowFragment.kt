package com.nelsongan.dentistathome.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.adapters.appointment.AppointmentAdapter
import com.nelsongan.dentistathome.adapters.review.ReviewAdapter
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentUserShowBinding
import com.nelsongan.dentistathome.models.Appointment
import com.nelsongan.dentistathome.models.Review
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils
import java.text.SimpleDateFormat

class UserShowFragment : Fragment() {
    private lateinit var binding: FragmentUserShowBinding
    private lateinit var user: User
    private lateinit var profileUser: User
    private var reviews : ArrayList<Review> = arrayListOf()

    // Get argument (user id)
    val args: UserShowFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user
        loadUser()

        // Load profile
        loadProfile()

        // Add event listeners
        addEventListeners()
    }

    private fun addEventListeners() {
        with (binding) {
            // Initialise adapter
            reviewRecyclerView.adapter = ReviewAdapter(view?.context!!, reviews)

            // Navigate to write review page
            btnWriteReview.setOnClickListener { view ->
                val action = UserShowFragmentDirections.actionUserShowFragmentToAddReviewFragment(profileUser.userId!!)
                view.findNavController().navigate(action)
            }

            // Pull down to refresh
            swipeLayout.setOnRefreshListener {
                loadReviews()
            }
        }
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
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

                    // Populate profile
                    populateProfile()
                }
            }
    }

    private fun populateProfile() {
        // Populate the fields
        with (binding) {
            txtName.text = profileUser.name

            // Set join date
            if (profileUser.createdAt != null) {
                val formattedDate: String = "Joined on " + SimpleDateFormat("MMMM yyyy").format(profileUser.createdAt!!)
                txtUserJoinedAt.text = formattedDate
            }

            // If user is dentist
            if (profileUser.role == "Dentist") {
                imgDentist.visibility = View.VISIBLE
            }

            // Show write review if not viewing own profile
            if (profileUser.userId != user.userId) {
                btnWriteReview.visibility = View.VISIBLE
            }

            // Load reviews
            loadReviews()
        }
    }

    private fun loadReviews() {
        // Clear review list
        reviews = arrayListOf()

        // Retrieve reviews from Firebase
        FirebaseUtils().firestore
            .collection("reviews")
            .whereEqualTo("userId", profileUser.userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result.documents) {
                        // Add review to ArrayList
                        val review: Review = document.toObject<Review>()!!
                        review.reviewId = document.id
                        reviews.add(review)
                    }

                    // Initialise views if review is found
                    if (reviews.size != 0) {
                        binding.txtEmpty.visibility = View.GONE
                    } else {
                        binding.txtEmpty.visibility = View.VISIBLE
                    }

                    // Sort reviews
                    reviews.sortByDescending {
                        it.createdAt
                    }

                    // Update recycler view
                    binding.reviewRecyclerView.adapter = ReviewAdapter(view?.context!!, reviews)

                    // Remove refreshing
                    binding.swipeLayout.isRefreshing = false
                } else {
                    Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}