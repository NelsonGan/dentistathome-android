package com.nelsongan.dentistathome.models

import java.time.LocalDate
import java.util.*

data class Inspection(
    var inspectionId: String? = null,
    var userId: String? = null,
    var issueDescription: String? = null,
    var issueDuration: String? = null,
    var issuePreviousOccurrence: String? = null,
    var imageUrl: String? = null,
    var reviewed: Boolean = false,
    var dentistId: String? = null,
    var dentistName: String? = null,
    var consultation: String? = null,
    var reviewedAt: Date? = null,
    var createdAt: Date? = null,
)
