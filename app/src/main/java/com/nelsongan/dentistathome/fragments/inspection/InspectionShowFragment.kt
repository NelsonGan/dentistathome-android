package com.nelsongan.dentistathome.fragments.inspection

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.databinding.FragmentInspectionShowBinding
import com.nelsongan.dentistathome.models.Inspection
import com.nelsongan.dentistathome.utils.FirebaseUtils
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class InspectionShowFragment : Fragment() {
    private lateinit var binding: FragmentInspectionShowBinding

    private lateinit var inspection: Inspection

    // Get argument (inspection id)
    val args: InspectionShowFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInspectionShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInspection()
    }

    private fun loadInspection() {
        val inspectionId : String = args.inspectionId

        // Load information if inspection id is present
        if (inspectionId.isNotEmpty()) {
            FirebaseUtils().firestore
                .collection("inspections")
                .document(inspectionId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Grab the inspection object
                        inspection = documentSnapshot.toObject<Inspection>()!!
                        inspection.inspectionId = documentSnapshot.id

                        // Populate inspection fields
                        populateInspection()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun populateInspection() {
        with (binding) {
            // Show image
            Picasso.with(context).load(inspection.imageUrl).into(imgIssue)

            // Load date created
            if (inspection.createdAt != null) {
                val formattedDate: String = SimpleDateFormat("MMMM dd, yyyy hh.mm aa").format(inspection.createdAt!!)
                txtCreatedAt.text = formattedDate
            }

            // Load issues
            txtIssueDescription.text = inspection.issueDescription
            txtIssueDuration.text = inspection.issueDuration
            txtIssuePreviousOccurrence.text = inspection.issuePreviousOccurrence

            // If reviewed
            if (inspection.reviewed) {
                // Show verified button
                imgVerified.visibility = View.VISIBLE

                // Show reviewed by section
                txtDentistName.visibility = View.VISIBLE
                txtDentistReviewedAt.visibility = View.VISIBLE
                imgProfile.visibility = View.VISIBLE
                txtConsultation.visibility = View.VISIBLE
                divider3.visibility = View.VISIBLE

                // Add event listener to point to dentist profile
                imgProfile.setOnClickListener { view ->
                    val action = InspectionShowFragmentDirections.actionInspectionShowFragmentToUserShowFragment(inspection.dentistId!!)
                    view.findNavController().navigate(action)
                }

                // Load date reviewed
                if (inspection.reviewedAt != null) {
                    val formattedDate: String = SimpleDateFormat("MMMM dd, yyyy hh.mm aa").format(inspection.reviewedAt!!)
                    txtDentistReviewedAt.text = formattedDate
                }

                // Load consultation text
                txtConsultation.text = inspection.consultation

                // Load dentist name
                txtDentistName.text = inspection.dentistName
            }
        }
    }
}