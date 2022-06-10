package com.nelsongan.dentistathome.fragments.inspection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.firestore.FieldValue
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentInspectionCreateBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils
import java.io.IOException
import java.util.*

private const val PICK_IMAGE_REQUEST: Int = 22

class InspectionCreateFragment : Fragment() {
    private lateinit var binding: FragmentInspectionCreateBinding
    private lateinit var user: User

    private var imageUri: Uri? = null
    private lateinit var fileUrlOnServer: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInspectionCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUser()
        addEventListeners()
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    private fun addEventListeners() {
        with (binding) {
            buttonUpload.setOnClickListener {
                selectImage()
            }

            buttonSubmit.setOnClickListener {
                createInspection()
            }
        }
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                // Get the URI of uploaded image
                imageUri = data.data
                try {
                    // Preview the uploaded image
                    binding.imageDisplay.setImageURI(imageUri)
                    binding.imageDisplay.visibility = View.VISIBLE

                    // Hide upload button and show image & submit button
                    binding.buttonUpload.visibility = View.GONE
                    binding.buttonSubmit.visibility = View.VISIBLE
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val issueDescriptionValidation: Boolean =
            binding.inputIssueDescription.text.toString().trim().isNotEmpty()
        val issueDurationValidation: Boolean =
            binding.inputIssueDuration.text.toString().trim().isNotEmpty()
        val issuePreviousOccurrenceValidation: Boolean =
            binding.inputIssuePreviousOccurrence.text.toString().trim().isNotEmpty()

        // Validation Message
        val blankMessage: String = "This field cannot be blank"

        if (!issueDescriptionValidation) {
            binding.inputIssueDescription.error = blankMessage
        }

        if (!issueDurationValidation) {
            binding.inputIssueDuration.error = blankMessage
        }

        if (!issuePreviousOccurrenceValidation) {
            binding.inputIssuePreviousOccurrence.error = blankMessage
        }

        if (!issueDescriptionValidation || !issueDurationValidation || !issuePreviousOccurrenceValidation) {
            Toast.makeText(activity, "Please try again!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createInspection() {
        if (validateInputFields()) {
            // Get input
            val issueDescription: String = binding.inputIssueDescription.text.toString().trim()
            val issueDuration: String = binding.inputIssueDuration.text.toString().trim()
            val issuePreviousOccurrence: String = binding.inputIssuePreviousOccurrence.text.toString().trim()
            val reviewed: Boolean = false

            // Upload image
            val filePath = "/user-images/" + user.userId + "/" + UUID.randomUUID().toString()
            FirebaseUtils().storage
                .reference
                .child(filePath)
                .putFile(imageUri!!)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Get uploaded file's URL
                        FirebaseUtils().storage.reference
                            .child(filePath)
                            .downloadUrl
                            .addOnCompleteListener { downloadUrl ->
                                if (downloadUrl.isSuccessful) {
                                    fileUrlOnServer = downloadUrl.result.toString()

                                    // Adding the inspection to firebase
                                    val inspectionHashMap = hashMapOf(
                                        "userId" to user.userId,
                                        "issueDescription" to issueDescription,
                                        "issueDuration" to issueDuration,
                                        "issuePreviousOccurrence" to issuePreviousOccurrence,
                                        "imageUrl" to fileUrlOnServer,
                                        "reviewed" to reviewed,
                                        "createdAt" to FieldValue.serverTimestamp()
                                    )

                                    FirebaseUtils().firestore
                                        .collection("inspections")
                                        .add(inspectionHashMap)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Go to inspection index page
                                                view?.findNavController()?.navigate(R.id.action_inspectionCreateFragment_to_inspectionIndexFragment)
                                                Toast.makeText(activity, "Inspection request created successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                // Error handling
                                                Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(activity, downloadUrl.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}