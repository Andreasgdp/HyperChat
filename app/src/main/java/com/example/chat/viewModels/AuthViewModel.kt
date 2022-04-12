package com.example.chat.viewModels

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
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
    val googleSignInClient = repository.googleSignInClient;

    fun register(email: String?, pass: String?, username: String?) {
        repository.register(email, pass, username)
    }

    fun signIn(email: String?, pass: String?) {
        repository.login(email, pass)
    }

    fun checkSignedIn() {
        repository.checkSignedIn()
    }

    fun signInWithGoogle(activity: Activity, data: Intent?) {
        repository.signInWithGoogle(activity, data)
    }

    fun signOut() {
        repository.signOut()
    }

}