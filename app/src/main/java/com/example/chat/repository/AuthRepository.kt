package com.example.chat.repository

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.chat.R
import com.example.chat.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
        if (auth.currentUser != null) {
            val currUser = User(email = auth.currentUser!!.email)
            firebaseUserMutableLiveData.postValue(currUser)
        }

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
                authDoneMutableLiveData.postValue(true)
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

                val currUser = User(email = email, password = pass)

                firebaseUserMutableLiveData.postValue(currUser)
                userLoggedOutMutableLiveData.postValue(false)
                authDoneMutableLiveData.postValue(true)
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

    fun signInWithGoogle(activity: Activity, data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { signIn ->
                    if (signIn.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser

                        val currUser = User()
                        currUser.userId = user?.uid
                        currUser.userName = user?.displayName
                        currUser.profilePic = user?.photoUrl.toString()

                        user?.uid?.let { usersRef.child(it).setValue(currUser) }

                        firebaseUserMutableLiveData.postValue(currUser)
                        userLoggedOutMutableLiveData.postValue(false)
                        authDoneMutableLiveData.postValue(true)
                    } else {
                        authDoneMutableLiveData.postValue(true)
                        Toast.makeText(application, signIn.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } catch (e: ApiException) {
        }
    }

}