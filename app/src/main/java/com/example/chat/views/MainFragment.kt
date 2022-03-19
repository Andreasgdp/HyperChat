package com.example.chat.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentMainBinding
import com.example.chat.viewModels.AuthViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var fragmentSignUpBinding: FragmentMainBinding? = null
    private val viewModel: AuthViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        fragmentSignUpBinding = binding

        viewModel.loggedOutStatus.observe(
            viewLifecycleOwner
        ) { loggedOut ->
            if (loggedOut) {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignInFragment())
            }
        }

        binding.btnSignOut.setOnClickListener { viewModel.signOut() }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignUpBinding = null
    }
}