package com.nelsongan.dentistathome.auth.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.databinding.ActivityCompleteRegistrationBinding
import com.nelsongan.dentistathome.databinding.ActivityRegisterBinding

class CompleteRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {
        supportActionBar?.hide()

        // Populate spinner
        val genders = resources.getStringArray(R.array.Genders)
        val genderArrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, genders)
        binding.inputGender.adapter = genderArrayAdapter

        val roles = resources.getStringArray(R.array.Roles)
        val roleArrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, roles)
        binding.inputRole.adapter = roleArrayAdapter
    }

    private fun addEventListeners() {

    }
}