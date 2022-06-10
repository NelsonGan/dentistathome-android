package com.nelsongan.dentistathome.fragments.inspection

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
import com.nelsongan.dentistathome.adapters.inspection.InspectionAdapter
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentInspectionReviewIndexBinding
import com.nelsongan.dentistathome.models.Inspection
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class InspectionReviewIndexFragment : Fragment() {
    private lateinit var binding: FragmentInspectionReviewIndexBinding
    private var inspections: ArrayList<Inspection> = arrayListOf()

    private lateinit var user: User
    private var showAll: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInspectionReviewIndexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUser()
        addEventListeners()
        loadInspections(showAll)
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    private fun addEventListeners() {
        with (binding) {
            inspectionsRecyclerView.adapter = InspectionAdapter(view?.context!!, inspections, true)

            swipeLayout.setOnRefreshListener {
                loadInspections(showAll)
            }

            btnToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.btn_all -> showAll = true
                        R.id.btn_reviewed -> showAll = false
                    }

                    loadInspections(showAll)
                }
            }
        }
    }

    private fun loadInspections(showAll : Boolean = false) {
        // Clear inspection list
        inspections = arrayListOf()

        if (showAll) {
            FirebaseUtils().firestore
                .collection("inspections")
                .whereEqualTo("reviewed", false)
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

                        // Update recycler view
                        binding.inspectionsRecyclerView.adapter = InspectionAdapter(view?.context!!, inspections, true)

                        // Remove refreshing
                        binding.swipeLayout.isRefreshing = false
                    } else {
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            FirebaseUtils().firestore
                .collection("inspections")
                .whereEqualTo("reviewed", true)
                .whereEqualTo("dentistId", user.userId)
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
                        binding.inspectionsRecyclerView.adapter = InspectionAdapter(view?.context!!, inspections, true)

                        // Remove refreshing
                        binding.swipeLayout.isRefreshing = false
                    } else {
                        Toast.makeText(activity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}