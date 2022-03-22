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
import com.example.chat.databinding.FragmentSignUpBinding
import com.example.chat.viewModels.AuthViewModel

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var fragmentSignUpBinding: FragmentSignUpBinding? = null
    private val viewModel: AuthViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignUpBinding.bind(view)
        fragmentSignUpBinding =  binding

        viewModel.userData.observe(
            viewLifecycleOwner
        ) { firebaseUser ->
            if (firebaseUser != null) {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToMainFragment())
            }
        }

        viewModel.authDone.observe(viewLifecycleOwner) {
            binding.loadingBar.isVisible = false
        }

        binding.textSignin.setOnClickListener {findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())}

        binding.btnSignup.setOnClickListener(View.OnClickListener {
            val email: String = binding.inputEmail.text.toString()
            val pass: String = binding.inputPassword.text.toString()
            val username: String = binding.inputUsername.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.register(email, pass, username)
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
            fragmentSignUpBinding?.loadingBar?.isVisible = true
            activity?.let { viewModel.signInWithGoogle(it, data) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSignUpBinding = null
    }
}