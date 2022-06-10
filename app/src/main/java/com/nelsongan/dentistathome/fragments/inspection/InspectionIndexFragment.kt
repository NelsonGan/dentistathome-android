package com.nelsongan.dentistathome.fragments.inspection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.adapters.inspection.InspectionAdapter
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentInspectionIndexBinding
import com.nelsongan.dentistathome.models.Inspection
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class InspectionIndexFragment : Fragment() {
    private lateinit var binding: FragmentInspectionIndexBinding
    private var inspections: ArrayList<Inspection> = arrayListOf()
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInspectionIndexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUser()
        addEventListeners()
        loadInspections()
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    private fun addEventListeners() {
        with (binding) {
            // Initialise adapter
            inspectionsRecyclerView.adapter = InspectionAdapter(view?.context!!, inspections)

            buttonAdd.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_inspectionIndexFragment_to_inspectionCreateFragment)
            }

            swipeLayout.setOnRefreshListener {
                loadInspections()
            }
        }
    }

    private fun loadInspections() {
        // Clear inspection list
        inspections = arrayListOf()

        // Retrieve inspections from Firebase
        FirebaseUtils().firestore
            .collection("inspections")
            .whereEqualTo("userId", user.userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result.documents) {
                        // Add inspection to ArrayList
                        val inspection : Inspection = document.toObject<Inspection>()!!
                        inspection.inspectionId = document.id
                        inspections.add(inspection)
                    }
                    // Initialise views if inspection is found
                    if (inspections.size != 0) {
                        binding.txtEmpty.visibility = View.GONE
                    } else {
                        binding.txtEmpty.visibility = View.VISIBLE
                    }

                    // Sort inspections
                    inspections.sortByDescending {
                        it.createdAt
                    }

                    // Update recycler view
                    binding.inspectionsRecyclerView.adapter = InspectionAdapter(view?.context!!, inspections)

                    // Remove refreshing
                    binding.swipeLayout.isRefreshing = false
                } else {
                    Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}