package com.example.chat.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.R
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.repository.UserService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userService = UserService()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // switch case in kotlin
        when (item.itemId) {
            R.id.menu_settings -> {
                Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_grup_chat -> {
                Toast.makeText(applicationContext, "Group Chat", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_log_out -> {
                userService.signOut(object : UserService.SignOutCallback {
                    override fun onSuccess() {
                        goToActivity(SignInActivity::class.java)
                    }

                    override fun onFail(exception: String?) {
                        Toast.makeText(applicationContext, exception, Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else -> { // Note the block
                Toast.makeText(applicationContext, "Err in selecting option", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}