package com.nelsongan.dentistathome.fragments.inspection

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
import com.nelsongan.dentistathome.databinding.FragmentInspectionReviewBinding
import com.nelsongan.dentistathome.models.Inspection
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class InspectionReviewFragment : Fragment() {
    private lateinit var binding: FragmentInspectionReviewBinding
    private lateinit var inspection: Inspection
    private lateinit var patient: User
    private lateinit var dentist: User

    // Get argument (inspection id)
    val args: InspectionReviewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInspectionReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUser()
        loadInspection()
        addEventListeners()
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            dentist = UserConstants.getUser(it)
        }

        binding.txtUserName
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

                        // Populate inspection details
                        populateInspection()

                        // Load patient
                        loadPatient()
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
        }
    }

    private fun loadPatient() {
        if (inspection.userId != null) {
            FirebaseUtils().firestore
                .collection("users")
                .document(inspection.userId!!)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Grab the user object
                        patient = documentSnapshot.toObject<User>()!!
                        patient.userId = documentSnapshot.id

                        // Populate user details
                        populatePatient()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun populatePatient() {
        with (binding) {
            txtUserName.text = patient.name

            // Set join date
            if (patient.createdAt != null) {
                val formattedDate: String = "Joined on " + SimpleDateFormat("MMMM yyyy").format(patient.createdAt!!)
                txtUserJoinedAt.text = formattedDate
            }

            // Add event listener to point to patient profile
            imgProfile.setOnClickListener { view ->
                val action = InspectionReviewFragmentDirections.actionInspectionReviewFragmentToUserShowFragment(patient.userId!!)
                view.findNavController().navigate(action)
            }
        }
    }

    private fun addEventListeners() {
        with (binding) {
            btnSubmit.setOnClickListener {
                reviewInspection()
            }
        }
    }

    private fun validateInputFields(): Boolean {
        val consultationValidation: Boolean = binding.inputConsultation.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!consultationValidation) {
            binding.inputConsultation.error = blankMessage
        }

        if (!consultationValidation) {
            Toast.makeText(activity, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun reviewInspection() {
        if (validateInputFields()) {
            // Get input
            val consultation: String = binding.inputConsultation.text.toString().trim()
            val bookAppointment: Boolean = binding.cbxBookAppointment.isChecked

            // Create appointment
            if (bookAppointment) {
                val appointmentHashMap = hashMapOf(
                    "senderId" to dentist.userId,
                    "senderName" to dentist.name,
                    "senderPhoneNumber" to dentist.phoneNumber,
                    "recipientId" to patient.userId,
                    "recipientName" to patient.name,
                    "recipientPhoneNumber" to patient.phoneNumber,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                FirebaseUtils().firestore
                    .collection("appointments")
                    .add(appointmentHashMap)
            }

            // Update inspection
            val inspectionHashMap = hashMapOf(
                "reviewed" to true,
                "dentistId" to dentist.userId,
                "dentistName" to dentist.name,
                "consultation" to consultation,
                "reviewedAt" to FieldValue.serverTimestamp()
            )

            FirebaseUtils().firestore
                .collection("inspections")
                .document(inspection.inspectionId!!)
                .update(inspectionHashMap as Map<String, Any>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Go to inspection review index
                        Toast.makeText(activity, "You've successfully reviewed the inspection!", Toast.LENGTH_SHORT).show()
                        view?.findNavController()?.navigate(R.id.action_inspectionReviewFragment_to_inspectionReviewIndexFragment)
                    } else {
                        // Error handling
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }


        }
    }
}