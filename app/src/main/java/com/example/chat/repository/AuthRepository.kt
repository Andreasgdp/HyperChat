package com.example.chat.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.chat.R
import com.example.chat.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AuthRepository(private val application: Application) {
    val firebaseUserMutableLiveData: MutableLiveData<User?> = MutableLiveData()
    val userLoggedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val authDoneMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database =
        Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")
    var googleSignInClient: GoogleSignInClient

    init {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(application, gso)
    }

    fun register(email: String?, pass: String?, username: String?) {
        auth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currUser = User(userName = username, email, pass)
                task.result?.user?.uid?.let { uid ->
                    usersRef.child(uid).setValue(currUser)
                    currUser.userId = uid
                }

                firebaseUserMutableLiveData.postValue(currUser)
                userLoggedOutMutableLiveData.postValue(false)
            } else {
                authDoneMutableLiveData.postValue(true)
                Toast.makeText(application, task.exception.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun login(email: String?, pass: String?) {
        auth.signInWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { signIn ->
            if (signIn.isSuccessful) {

                val currUser = User(email = email,password =  pass)

                firebaseUserMutableLiveData.postValue(currUser)
                userLoggedOutMutableLiveData.postValue(false)
            } else {
                authDoneMutableLiveData.postValue(true)
                Toast.makeText(application, signIn.exception.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        userLoggedOutMutableLiveData.postValue(true)
    }

    init {
        if (auth.currentUser != null) {
            val currUser = User(email = auth.currentUser!!.email)
            firebaseUserMutableLiveData.postValue(currUser)
        }
    }
}