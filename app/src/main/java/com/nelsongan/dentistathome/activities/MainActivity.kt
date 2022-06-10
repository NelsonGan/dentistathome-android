package com.nelsongan.dentistathome.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.ActivityMainBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var currentUser: FirebaseUser? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Get user
        loadUser()

        // Inflate layout with binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise views & navigation
        initialiseView()
        setupBottomNavigationBar()
    }

    private fun initialiseView() {
        navController = findNavController(R.id.nav_controller)
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun authenticationCheck() {
        if (currentUser != null)  {
            // Login user
            // Add user to shared preference
            UserConstants.saveUser(this, user)

            // Navigate to home page
            navController.navigate(R.id.action_global_homeFragment)
        } else {
            // Logout user
            // Clear user from shared preference
            UserConstants.clearUserFromSharedPreference(this)

            // Navigate to login page
            navController.navigate(R.id.action_global_loginFragment)
        }
    }

    private fun loadUser(authCheck: Boolean = true) {
        currentUser = FirebaseUtils().auth.currentUser

        if (currentUser != null) {
            FirebaseUtils().firestore
                .collection("users")
                .document(currentUser!!.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    // Set the user object
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject<User>()!!
                        user.userId = documentSnapshot.id

                        // Perform auth check
                        if (authCheck) authenticationCheck()

                        // Refresh navigation bar routing
                        setupBottomNavigationBarRouting()
                    }
                }
        }
    }

    private fun setupBottomNavigationBar() {
        // Hide bottom navigation bar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Hide bottom navigation bar
                R.id.loginFragment -> binding.bottomNavigation.visibility = View.GONE
                R.id.registerFragment -> binding.bottomNavigation.visibility = View.GONE
                R.id.completeRegistrationFragment -> binding.bottomNavigation.visibility = View.GONE
                else -> binding.bottomNavigation.visibility = View.VISIBLE
            }

            // Refresh user
            when (destination.id) {
                R.id.loginFragment -> loadUser(false)
                R.id.homeFragment -> loadUser(false)
            }
        }
    }

    private fun setupBottomNavigationBarRouting() {
        // Setup nav bar routing
        binding.bottomNavigation.setOnItemSelectedListener {
            // Conditional routing
            if (user.role == "User") {
                when (it.itemId) {
                    R.id.home -> navController.navigate(R.id.action_global_homeFragment)
                    R.id.inspections -> navController.navigate(R.id.action_global_inspectionIndexFragment)
                    R.id.appointments -> navController.navigate(R.id.action_global_appointmentIndexFragment)
                    R.id.profile -> navController.navigate(R.id.action_global_profileFragment)
                }
            } else if (user.role == "Dentist") {
                when (it.itemId) {
                    R.id.home -> navController.navigate(R.id.action_global_homeFragment)
                    R.id.inspections -> navController.navigate(R.id.action_global_inspectionReviewIndexFragment)
                    R.id.appointments -> navController.navigate(R.id.action_global_appointmentIndexFragment)
                    R.id.profile -> navController.navigate(R.id.action_global_profileFragment)
                }
            }

            true
        }
    }
}