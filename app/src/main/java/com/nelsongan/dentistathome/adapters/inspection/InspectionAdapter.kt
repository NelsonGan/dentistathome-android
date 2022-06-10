package com.nelsongan.dentistathome.adapters.inspection

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nelsongan.dentistathome.databinding.ListInspectionItemBinding
import com.nelsongan.dentistathome.fragments.inspection.InspectionIndexFragmentDirections
import com.nelsongan.dentistathome.fragments.inspection.InspectionReviewIndexFragmentDirections
import com.nelsongan.dentistathome.models.Inspection
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat


class InspectionAdapter(
    private val context: Context,
    private val inspections: List<Inspection>,
    private val forDentist: Boolean = false,
) : RecyclerView.Adapter<InspectionAdapter.InspectionViewHolder>() {

    inner class InspectionViewHolder(val binding: ListInspectionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Size of data
    override fun getItemCount(): Int = inspections.size

    // Create view holder which host a single list item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectionViewHolder {
        val binding = ListInspectionItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return InspectionViewHolder(binding)
    }

    // Bind the data to the view holder programmatically
    override fun onBindViewHolder(holder: InspectionViewHolder, position: Int) {
        val inspection = inspections[position]

        // Data binding
        with(holder) {
            with (binding) {
                // Image
                Picasso.with(context).load(inspection.imageUrl).into(imgIssue)

                // Format date
                if (inspection.createdAt != null) {
                    val formattedDate: String = SimpleDateFormat("MMMM dd, yyyy hh.mm aa").format(inspection.createdAt!!)
                    txtDate.text = formattedDate
                }

                // Issue description
                if (inspection.issueDescription?.length!! > 40) {
                    val slicedIssueDescription : String = inspection.issueDescription?.slice(0..40) +  "..."
                    txtIssueDescription.text = slicedIssueDescription
                } else {
                    txtIssueDescription.text = inspection.issueDescription
                }

                // Does not need to show dentist name if showing to dentist
                if (!forDentist) {
                    // If inspection is reviewed
                    if (inspection.reviewed) {
                        // Show dentist name
                        imgProfile.visibility = View.VISIBLE
                        txtDentistName.visibility = View.VISIBLE

                        // Populate dentist name
                        txtDentistName.text = inspection.dentistName

                        // Colour the bar with green
                        imgReviewed.setBackgroundColor(Color.GREEN)
                    }
                } else {
                    if (inspection.reviewed) {
                        // Colour the bar with green
                        imgReviewed.setBackgroundColor(Color.GREEN)
                    }
                }

                // Conditional routing depending if using for review
                if (!forDentist) {
                    inspectionCardView.setOnClickListener { View ->
                        val action = InspectionIndexFragmentDirections.actionInspectionIndexFragmentToInspectionShowFragment(inspection.inspectionId!!)
                        View.findNavController().navigate(action)
                    }
                } else {
                    // Route to review if inspection is not reviewed
                    if (!inspection.reviewed) {
                        inspectionCardView.setOnClickListener { View ->
                            val action = InspectionReviewIndexFragmentDirections.actionInspectionReviewIndexFragmentToInspectionReviewFragment(inspection.inspectionId!!)
                            View.findNavController().navigate(action)
                        }
                    }
                }

            }
        }
    }

}