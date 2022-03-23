package com.example.chat.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.repository.UserService
import utils.ActivityRouting

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userService: UserService
    private lateinit var activityRouting: ActivityRouting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRouting = ActivityRouting(this)
        userService = UserService(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
    }
}