package com.nelsongan.dentistathome.misc.screens

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseUser
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.auth.screens.CompleteRegistrationActivity
import com.nelsongan.dentistathome.auth.screens.LoginActivity
import com.nelsongan.dentistathome.databinding.ActivityMenuBinding
import com.nelsongan.dentistathome.models.User
import com.nelsongan.dentistathome.utils.FirebaseUtils


class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        authCheck()
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.button_logout -> {
                // Logout & Navigate to Login Page
                FirebaseUtils().auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun runViewBinding() {
        binding = ActivityMenuBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {
        // Set toolbar
        val toolbar: Toolbar = binding.toolbar.toolbar
        setSupportActionBar(toolbar)
    }

    private fun addEventListeners() {

    }

    private fun authCheck() {
        // Get current user
        val firebaseUser: FirebaseUser? = FirebaseUtils().auth.currentUser

        // Not signed in
        if (firebaseUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}