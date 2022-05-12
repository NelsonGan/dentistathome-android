package com.nelsongan.dentistathome.users.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nelsongan.dentistathome.databinding.ActivityInspectionIndexBinding
import com.nelsongan.dentistathome.databinding.ActivityInspectionShowBinding

class InspectionShowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInspectionShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityInspectionShowBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {

    }

    private fun addEventListeners() {

    }
}