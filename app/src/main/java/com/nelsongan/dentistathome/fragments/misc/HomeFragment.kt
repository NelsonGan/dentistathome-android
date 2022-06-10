package com.nelsongan.dentistathome.fragments.misc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.nelsongan.dentistathome.R
import com.nelsongan.dentistathome.constants.UserConstants
import com.nelsongan.dentistathome.databinding.FragmentHomeBinding
import com.nelsongan.dentistathome.models.User

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUser()
        loadHomePage()
    }

    private fun loadUser() {
        // Get user from shared preference
        activity?.let {
            user = UserConstants.getUser(it)
        }
    }

    fun loadHomePage() {
        with (binding) {
            if (user.role == "User") {
                // Show and activate button
                btnSchedule.visibility = View.VISIBLE
                btnSchedule.setOnClickListener {
                    view?.findNavController()?.navigate(R.id.action_global_inspectionIndexFragment)
                }
            }
        }
    }
}