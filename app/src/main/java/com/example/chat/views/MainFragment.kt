package com.example.chat.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentMainBinding
import com.example.chat.viewModels.AuthViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var fragmentSignUpBinding: FragmentMainBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener {
            // switch case in kotlin
            when (it.itemId) {
                R.id.menu_settings -> {
                    Toast.makeText(requireActivity(), "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_grup_chat -> {
                    Toast.makeText(requireActivity(), "Group Chat", Toast.LENGTH_SHORT).show()
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

        return view
    }

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignUpBinding = null
    }
}