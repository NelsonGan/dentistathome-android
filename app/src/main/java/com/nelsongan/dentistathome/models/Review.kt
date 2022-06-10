package com.nelsongan.dentistathome.models

import java.util.*

data class Review(
    var reviewId: String? = null,
    var userId: String? = null,
    var reviewerId: String? = null,
    var reviewerName: String? = null,
    var reviewContent: String? = null,
    var recommended: Boolean? = false,
    var createdAt: Date? = null,
)
