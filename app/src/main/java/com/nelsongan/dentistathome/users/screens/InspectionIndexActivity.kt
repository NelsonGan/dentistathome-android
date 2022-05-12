package com.nelsongan.dentistathome.users.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nelsongan.dentistathome.databinding.ActivityInspectionCreateBinding
import com.nelsongan.dentistathome.databinding.ActivityInspectionIndexBinding

class InspectionIndexActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInspectionIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityInspectionIndexBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {

    }

    private fun addEventListeners() {

    }
}