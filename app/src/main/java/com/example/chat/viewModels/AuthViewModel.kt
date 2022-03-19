package com.example.chat.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chat.models.User
import com.example.chat.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AuthRepository = AuthRepository(application)
    var userData: MutableLiveData<User?> = repository.firebaseUserMutableLiveData
    val loggedOutStatus: MutableLiveData<Boolean> = repository.userLoggedOutMutableLiveData
    val authDone: MutableLiveData<Boolean> = repository.authDoneMutableLiveData

    fun register(email: String?, pass: String?, username: String?) {
        repository.register(email, pass, username)
    }

    fun signIn(email: String?, pass: String?) {
        repository.login(email, pass)
    }

    fun signOut() {
        repository.signOut()
    }

}