package com.example.chat.views

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.chat.databinding.ActivitySignUpBinding
import com.example.chat.models.User
import com.example.chat.repository.UserService
import utils.ActivityRouting

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userService: UserService
    private lateinit var activityRouting: ActivityRouting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRouting = ActivityRouting(this)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // don't show progressbar
        binding.loadingBar.isVisible = false

        userService = UserService()

        supportActionBar?.hide()

        binding.btnSignup.setOnClickListener { signUp() }
        binding.textSignin.setOnClickListener { activityRouting.goToActivity(SignInActivity::class.java) }
    }

    private fun signUp() {
        if (binding.inputUsername.text.isNotEmpty() && binding.inputEmail.text.isNotEmpty() && binding.inputPassword.text.isNotEmpty()) {
            // bring down keyboard: only runs if there is a view that is currently focused
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

            // show progressbar while creating account
            binding.loadingBar.isVisible = true

            userService.createUserAndSignIn(
                binding.inputUsername.text.toString(),
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString(),
                object :
                    UserService.ResponseCallback {
                    override fun onSuccess(user: User?) {
                        Toast.makeText(applicationContext, "Sign Up Successful", Toast.LENGTH_SHORT)
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


}