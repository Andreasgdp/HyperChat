package com.example.chat.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chat.R
import com.example.chat.databinding.FragmentSignInBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var fragmentSignInBinding: FragmentSignInBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignInBinding.bind(view)
        fragmentSignInBinding =  binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignInBinding = null
    }
}