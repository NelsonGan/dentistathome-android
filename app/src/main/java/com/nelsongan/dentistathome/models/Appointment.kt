package com.nelsongan.dentistathome.models

import java.util.*

data class Appointment(
    var appointmentId: String? = null,
    var senderId: String? = null,
    var senderName: String? = null,
    var senderPhoneNumber: String? = null,
    var recipientId: String? = null,
    var recipientName: String? = null,
    var recipientPhoneNumber: String? = null,
    var createdAt: Date? = null,
)

