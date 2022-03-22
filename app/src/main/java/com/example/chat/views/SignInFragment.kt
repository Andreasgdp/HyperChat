package com.example.chat.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentSignInBinding
import com.example.chat.viewModels.AuthViewModel


class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var fragmentSignInBinding: FragmentSignInBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignInBinding.bind(view)
        fragmentSignInBinding = binding

        viewModel.userData.observe(
            viewLifecycleOwner
        ) { firebaseUser ->
            if (firebaseUser != null) {
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
            }
        }

        viewModel.authDone.observe(viewLifecycleOwner) {
            binding.loadingBar.isVisible = false
        }

        binding.textSignup.setOnClickListener {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            )
        }

        binding.btnSignin.setOnClickListener(View.OnClickListener {
            val email: String = binding.inputEmail.text.toString()
            val pass: String = binding.inputPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                binding.loadingBar.isVisible = true
                viewModel.signIn(email, pass)
            } else {
                Toast.makeText(activity, "Enter Credentials!", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
    }

    private val RC_SIGN_IN = 65
    private fun signInWithGoogle() {
        startActivityForResult(viewModel.googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // show progressbar while creating account
            fragmentSignInBinding?.loadingBar?.isVisible = true
            activity?.let { viewModel.signInWithGoogle(it, data) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignInBinding = null
    }
}