package com.example.chat.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.chat.R
import com.example.chat.databinding.ActivitySignInBinding
import com.example.chat.models.User
import com.example.chat.repository.UserService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import utils.ActivityRouting

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var userService: UserService
    private lateinit var activityRouting: ActivityRouting
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRouting = ActivityRouting(this)

        userService = UserService()
        if (userService.isLoggedIn()) {
            // Move to main activity if user is logged in
            activityRouting.clearCurrentAndGoToActivity(MainActivity::class.java)
        } else {
            // Load sign in activity if user is not logged in
            binding = ActivitySignInBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            // don't show progressbar
            binding.loadingBar.isVisible = false

            supportActionBar?.hide()

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)

            binding.btnSignin.setOnClickListener { signIn() }
            binding.textSignup.setOnClickListener { activityRouting.goToActivity(SignUpActivity::class.java) }
            binding.btnGoogle.setOnClickListener { signInGoogle() }
        }
    }

    private fun signIn() {
        if (binding.inputEmail.text.isNotEmpty() && binding.inputPassword.text.isNotEmpty()) {
            // bring down keyboard: only runs if there is a view that is currently focused
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

            // show progressbar while creating account
            binding.loadingBar.isVisible = true

            // Log User in.
            userService.signIn(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString(),
                object :
                    UserService.ResponseCallback {
                    override fun onSuccess(user: User?) {
                        Toast.makeText(applicationContext, "Sign In Successful", Toast.LENGTH_SHORT)
                            .show()
                        activityRouting.clearCurrentAndGoToActivity(MainActivity::class.java)
                    }

                    override fun onFail(exception: String?) {
                        Toast.makeText(applicationContext, exception, Toast.LENGTH_SHORT).show()
                    }

                    override fun onComplete() {
                        binding.loadingBar.isVisible = false
                    }

                })
        } else {
            Toast.makeText(applicationContext, "Enter Credentials!", Toast.LENGTH_SHORT).show()
        }
    }

    private val RC_SIGN_IN = 65
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // show progressbar while creating account
            binding.loadingBar.isVisible = true
            userService.signInWithGoogle(this, data, object: UserService.ResponseCallback {
                override fun onSuccess(user: User?) {
                    Toast.makeText(applicationContext, "Sign In Successful", Toast.LENGTH_SHORT)
                        .show()
                    activityRouting.clearCurrentAndGoToActivity(MainActivity::class.java)
                }

                override fun onFail(exception: String?) {
                    Toast.makeText(applicationContext, exception, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onComplete() {
                    binding.loadingBar.isVisible = false
                }

            })
        }
    }
}