package com.example.chat.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.adapter.MainFragmentAdapter
import com.example.chat.databinding.FragmentMainBinding
import com.example.chat.viewModels.AuthViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var fragmentSignUpBinding: FragmentMainBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        fragmentSignUpBinding = binding

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener {
            // switch case in kotlin
            when (it.itemId) {
                R.id.menu_settings -> {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
                }
                R.id.menu_log_out -> {
                    viewModel.signOut()
                }
                else -> { // Default switch case
                    Toast.makeText(requireActivity(), "Err in selecting option", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        }

        viewModel.loggedOutStatus.observe(
            viewLifecycleOwner
        ) { loggedOut ->
            if (loggedOut) {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignInFragment())
            }
        }

        fragmentSignUpBinding!!.viewPager.adapter = MainFragmentAdapter(childFragmentManager)
        fragmentSignUpBinding!!.tabLayout.setupWithViewPager(fragmentSignUpBinding!!.viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignUpBinding = null
    }
}