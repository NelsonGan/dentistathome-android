package com.nelsongan.dentistathome.users.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nelsongan.dentistathome.databinding.ActivityInspectionCreateBinding
import com.nelsongan.dentistathome.databinding.ActivityMenuBinding

class InspectionCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInspectionCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewBinding()
        runUiSettings()
        addEventListeners()
        setContentView(binding.root)
    }

    private fun runViewBinding() {
        binding = ActivityInspectionCreateBinding.inflate(layoutInflater)
    }

    private fun runUiSettings() {

    }

    private fun addEventListeners() {

    }
}