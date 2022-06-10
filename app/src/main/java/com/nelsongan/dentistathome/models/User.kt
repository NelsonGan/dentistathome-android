package com.nelsongan.dentistathome.models

import java.time.LocalDate
import java.util.*

data class User(
    var userId: String? = null,
    var email: String? = null,
    var name: String? = null,
    var gender: String? = null,
    var role: String? = null,
    var addressLine1: String? = null,
    var addressLine2: String? = null,
    var phoneNumber: String? = null,
    var createdAt: Date? = null,
)

