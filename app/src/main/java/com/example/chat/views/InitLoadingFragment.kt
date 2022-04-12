package com.example.chat.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentInitLoadingBinding
import com.example.chat.viewModels.AuthViewModel


class InitLoadingFragment : Fragment(R.layout.fragment_init_loading) {

    private var fragmentInitLoadingBinding: FragmentInitLoadingBinding? = null
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInitLoadingBinding.bind(view)
        fragmentInitLoadingBinding = binding

        viewModel.checkSignedIn();

        viewModel.userData.observeForever { firebaseUser ->
            if (firebaseUser != null) {
                findNavController().navigate(InitLoadingFragmentDirections.actionInitLoadingFragmentToMainFragment())
            } else {
                findNavController().navigate(InitLoadingFragmentDirections.actionInitLoadingFragmentToSignInFragment())
            }
        }
    }
}