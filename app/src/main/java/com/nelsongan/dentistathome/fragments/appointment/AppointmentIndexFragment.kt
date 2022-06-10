package com.nelsongan.dentistathome.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.adapters.appointment.AppointmentAdapter
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentAppointmentIndexBinding
import com.nelsongan.dentistathome.models.Appointment
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class AppointmentIndexFragment : Fragment() {
    private lateinit var binding: FragmentAppointmentIndexBinding
    private var appointments : ArrayList<Appointment> = arrayListOf()
    private lateinit var user : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAppointmentIndexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUser()
        addEventListeners()
        loadAppointments()
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
            appointmentsRecyclerView.adapter = AppointmentAdapter(view?.context!!, appointments, user.userId!!)

            swipeLayout.setOnRefreshListener {
                loadAppointments()
            }
        }
    }

    private fun loadAppointments() {
        // Clear appointment list
        appointments = arrayListOf()

        // Retrieve appointment from Firebase (where current user is sender)
        FirebaseUtils().firestore
            .collection("appointments")
            .whereEqualTo("senderId", user.userId)
            .get()
            .addOnCompleteListener { senderTask ->
                if (senderTask.isSuccessful) {
                    for (document in senderTask.result.documents) {
                        // Add appointment to ArrayList
                        val appointment : Appointment = document.toObject<Appointment>()!!
                        appointment.appointmentId = document.id
                        appointments.add(appointment)
                    }

                    // Retrieve appointment from Firebase (where current user is recipient)
                    FirebaseUtils().firestore
                        .collection("appointments")
                        .whereEqualTo("recipientId", user.userId)
                        .get()
                        .addOnCompleteListener { recipientTask ->
                            if (recipientTask.isSuccessful) {
                                for (document in recipientTask.result.documents) {
                                    // Add appointment to ArrayList
                                    val appointment : Appointment = document.toObject<Appointment>()!!
                                    appointment.appointmentId = document.id
                                    appointments.add(appointment)
                                }

                                // Initialise views if appointment is found
                                if (appointments.size != 0) {
                                    binding.txtEmpty.visibility = View.GONE
                                } else {
                                    binding.txtEmpty.visibility = View.VISIBLE
                                }

                                // Sort appointments
                                appointments.sortByDescending {
                                    it.createdAt
                                }

                                // Update recycler view
                                binding.appointmentsRecyclerView.adapter = AppointmentAdapter(view?.context!!, appointments, user.userId!!)

                                // Remove refreshing
                                binding.swipeLayout.isRefreshing = false
                            } else {
                                Toast.makeText(activity, recipientTask.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    Toast.makeText(activity, senderTask.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}