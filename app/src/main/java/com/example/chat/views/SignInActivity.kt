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
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database =
        Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")

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
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    val ourUser = User()
                    ourUser.userId = user?.uid
                    ourUser.userName = user?.displayName
                    ourUser.profilePic = user?.photoUrl.toString()
                    user?.uid?.let { usersRef.child(it).setValue(ourUser) }
                    activityRouting.clearCurrentAndGoToActivity(MainActivity::class.java)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }
}