package com.nelsongan.dentistathome.models

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.nelsongan.dentistathome.utils.FirebaseUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class User(
    var userId: String,
    var email: String,
    var name: String? = null,
    var gender: String? = null,
    var role: String? = null,
    var addressLine1: String? = null,
    var addressLine2: String? = null,
    var createdAt: Date? = null,
) : Serializable

