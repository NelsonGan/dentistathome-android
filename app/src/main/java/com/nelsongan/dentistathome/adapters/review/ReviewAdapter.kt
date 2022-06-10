package com.nelsongan.dentistathome.adapters.review

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nelsongan.dentistathome.databinding.ListReviewItemBinding
import com.nelsongan.dentistathome.fragments.appointment.AppointmentIndexFragmentDirections
import com.nelsongan.dentistathome.fragments.user.UserShowFragmentDirections
import com.nelsongan.dentistathome.models.Review
import java.text.SimpleDateFormat

class ReviewAdapter(
    private val context: Context,
    private val reviews: List<Review>,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(val binding: ListReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Size of data
    override fun getItemCount(): Int = reviews.size

    // Create view holder which host a single list item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ListReviewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        with(holder) {
            with(binding) {
                // Populate data
                txtName.text = review.reviewerName
                txtDescription.text = review.reviewContent

                // Format date
                if (review.createdAt != null) {
                    val date: String = SimpleDateFormat("yyyy-MM-dd").format(review.createdAt!!)
                    txtDate.text = date
                }

                // Show recommended tag
                if (review.recommended == true) {
                    txtRecommended.visibility = View.VISIBLE
                } else {
                    txtNotRecommended.visibility = View.VISIBLE
                }

                // Set click listener on image
                imgProfile.setOnClickListener { view ->
                    val action = UserShowFragmentDirections.actionUserShowFragmentSelf(review.reviewerId!!)
                    view.findNavController().navigate(action)
                }
            }
        }
    }
}

