package com.example.chat.viewModels

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat.models.User
import com.example.chat.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebaseRepository = FirebaseRepository(application)
    var userData: MutableLiveData<User?> = repository.firebaseUserMutableLiveData
    val loggedOutStatus: MutableLiveData<Boolean> = repository.userLoggedOutMutableLiveData
    val authDone: MutableLiveData<Boolean> = repository.authDoneMutableLiveData
    val googleSignInClient = repository.googleSignInClient

    fun register(email: String?, pass: String?, username: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.register(email, pass, username)
        }
    }

    fun signIn(email: String?, pass: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(email, pass)
        }
    }

    fun checkSignedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkSignedIn()
        }
    }

    fun signInWithGoogle(activity: Activity, data: Intent?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signInWithGoogle(activity, data)
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signOut()
        }
    }

}