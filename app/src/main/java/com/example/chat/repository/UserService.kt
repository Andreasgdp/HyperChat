package com.example.chat.repository

import com.example.chat.models.User
import com.google.firebase.auth.FirebaseAuth
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
}