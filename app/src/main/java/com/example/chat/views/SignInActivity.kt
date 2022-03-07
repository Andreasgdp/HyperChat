package com.example.chat.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chat.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        getSupportActionBar()?.hide();
    }
}