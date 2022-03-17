package com.example.chat.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var fragmentSignUpBinding: FragmentSignUpBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignUpBinding.bind(view)
        fragmentSignUpBinding =  binding

        binding.textSignin.setOnClickListener {findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignUpBinding = null
    }
}