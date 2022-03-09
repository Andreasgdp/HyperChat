package com.example.chat.repository

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.chat.models.User
import com.example.chat.views.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread


class UserService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database =
        Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")

    interface ResponseCallback {
        fun onSuccess(user: User?)
        fun onFail(exception: String?)
        fun onComplete()
    }

    fun createUserAndSignIn(
        username: String,
        email: String,
        password: String,
        callback: ResponseCallback
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(username, email, password)

                // make sure that uid is available at use.
                task.result?.user?.uid?.let { uid ->
                    usersRef.child(uid).setValue(user)
                    user.userId = uid
                }
                signIn(email, password, object : ResponseCallback {
                    override fun onSuccess(user: User?) {
                        callback.onSuccess(user)
                    }

                    override fun onFail(exception: String?) {
                        callback.onFail(task.exception.toString())
                    }

                    override fun onComplete() {
                        callback.onComplete()
                    }
                })

            } else {
                callback.onFail(task.exception.toString())
            }

        }
    }

    fun signIn(
        email: String,
        password: String,
        callback: ResponseCallback
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(email, password)

                callback.onSuccess(user)
            } else {
                callback.onFail(task.exception.toString())
            }

            callback.onComplete()
        }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    interface SignOutCallback {
        fun onSuccess()
        fun onFail(exception: String?)
    }

    fun signOut(callback: SignOutCallback) {
        auth.signOut()
        thread {
            var count = 1
            while (isLoggedIn()) {
                Thread.sleep(100)
                if (count >= 10) {
                    callback.onFail("TimeOutException: Failed to sign out user")
                    return@thread
                }
                count++
            }
            callback.onSuccess()
        }
    }

    fun signInWithGoogle(activity: Activity, data: Intent?, callback: ResponseCallback){
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success")
                        val user = auth.currentUser
                        val ourUser = User()
                        ourUser.userId = user?.uid
                        ourUser.userName = user?.displayName
                        ourUser.profilePic = user?.photoUrl.toString()
                        user?.uid?.let { usersRef.child(it).setValue(ourUser) }
                        callback.onSuccess(ourUser)
                    } else {
                        callback.onFail(task.exception.toString())
                    }
                    callback.onComplete()
                }
        } catch (e: ApiException) {
            callback.onFail(e.toString())
        }
    }
}