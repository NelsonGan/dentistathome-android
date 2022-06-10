package com.nelsongan.dentistathome.adapters.appointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nelsongan.dentistathome.databinding.ListAppointmentItemBinding
import com.nelsongan.dentistathome.fragments.appointment.AppointmentIndexFragmentDirections
import com.nelsongan.dentistathome.fragments.inspection.InspectionShowFragmentDirections
import com.nelsongan.dentistathome.models.Appointment
import java.text.SimpleDateFormat

class AppointmentAdapter(
    private val context: Context,
    private val appointments: List<Appointment>,
    private val userId: String
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(val binding: ListAppointmentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Size of data
    override fun getItemCount(): Int = appointments.size

    // Create view holder which host a single list item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ListAppointmentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        with (holder) {
            with (binding) {
                // Set date
                if (appointment.createdAt != null) {
                    val time: String = SimpleDateFormat("hh.mm aa").format(appointment.createdAt!!)
                    val date: String = SimpleDateFormat("MMMM dd").format(appointment.createdAt!!)

                    txtTime.text = time
                    txtDate.text = date
                }

                // Description format
                // If user is sender
                if (appointment.senderId == userId) {
                    txtName.text = appointment.recipientName

                    // Set description
                    val description: String = "You have scheduled an appointment with " + appointment.recipientName + ", " +
                            "please call " + appointment.recipientPhoneNumber + " to confirm the appointment details."
                    txtDescription.text = description

                    // Add event listener to point to recipient profile
                    imgProfile.setOnClickListener { view ->
                        val action = AppointmentIndexFragmentDirections.actionAppointmentIndexFragmentToUserShowFragment(appointment.recipientId!!)
                        view.findNavController().navigate(action)
                    }
                } else {
                    // If user is recipient
                    txtName.text = appointment.senderName

                    // Set description
                    val description: String = appointment.senderName + " has scheduled an appointment with you, please call " +
                            appointment.senderPhoneNumber + " to confirm the appointment details."
                    txtDescription.text = description

                    // Add event listener to point to recipient profile
                    imgProfile.setOnClickListener { view ->
                        val action = AppointmentIndexFragmentDirections.actionAppointmentIndexFragmentToUserShowFragment(appointment.senderId!!)
                        view.findNavController().navigate(action)
                    }
                }
            }
        }
    }
}