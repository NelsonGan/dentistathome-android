package com.nelsongan.dentistathome.constants

import android.app.Activity
import android.content.Context
import com.nelsongan.dentistathome.models.User
import java.text.SimpleDateFormat
import java.util.*

class UserConstants() {
    companion object {
        fun saveData(activity: Activity, variable: String, data: String) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString(variable, data)
                apply()
            }
        }

        fun getData(activity: Activity, variable: String, defaultValue: String): String? {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            return sharedPref.getString(variable, defaultValue)
        }

        fun saveUser(activity: Activity, user: User) {
            // Save data
            user.userId?.let { saveData(activity, "userId", it) }
            user.email?.let { saveData(activity, "email", it) }
            user.name?.let { saveData(activity, "name", it) }
            user.gender?.let { saveData(activity, "gender", it) }
            user.role?.let { saveData(activity, "role", it) }
            user.addressLine1?.let { saveData(activity, "addressLine1", it) }
            user.addressLine2?.let { saveData(activity, "addressLine2", it) }
            user.phoneNumber?.let { saveData(activity, "phoneNumber", it) }

            // Format date
            user.createdAt?.let {
                val createdAtString: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.createdAt!!)
                saveData(activity, "createdAt", createdAtString)
            }
        }

        fun getUser(activity: Activity): User {
            // Get data
            var userId: String? = getData(activity, "userId", "")
            var email: String? = getData(activity, "email", "")
            var name: String? = getData(activity, "name", "")
            var gender: String? = getData(activity, "gender", "")
            var role: String? = getData(activity, "role", "")
            var addressLine1: String? = getData(activity, "addressLine1", "")
            var addressLine2: String? = getData(activity, "addressLine2", "")
            var phoneNumber: String? = getData(activity, "phoneNumber", "")

            // Parse string to date
            var createdAtString: String? = getData(activity, "createdAt", "")
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var createdAt: Date? = formatter.parse(createdAtString!!)

            // Return user object
            return User(
                userId,
                email,
                name,
                gender,
                role,
                addressLine1,
                addressLine2,
                phoneNumber,
                createdAt
            )
        }

        fun clearUserFromSharedPreference(activity: Activity) {
            // Clear from shared preference
            saveData(activity, "userId", "")
            saveData(activity, "email", "")
            saveData(activity, "name", "")
            saveData(activity, "gender", "")
            saveData(activity, "role", "")
            saveData(activity, "addressLine1", "")
            saveData(activity, "addressLine2", "")
            saveData(activity, "phoneNumber", "")
            saveData(activity, "createdAt", "")
        }
    }
}