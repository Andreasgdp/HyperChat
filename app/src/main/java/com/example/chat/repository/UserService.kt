package com.example.chat.repository

import android.util.Log
import com.example.chat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance();
    private val database = Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")

    interface ResponseCallback {
        fun onSuccess(user: User)
        fun onFail(exception: String?)
        fun onComplete()
    }

    fun createUser(
        username: String,
        email: String,
        password: String,
        callback: ResponseCallback
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(username, email, password);

                // make sure that uid is available at use.
                task.result?.user?.uid?.let { uid ->
                    usersRef.child(uid).setValue(user)
                    user.userId = uid;
                }

                callback.onSuccess(user);
            } else {
                callback.onFail(task.exception.toString());
            }

            callback.onComplete();
        };
    }

    fun logUserIn(
        email: String,
        password: String,
        callback: ResponseCallback
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(email, password);

                callback.onSuccess(user);
            } else {
                callback.onFail(task.exception.toString());
            }

            callback.onComplete();
        };
    }

    fun isLoggedIn(): Boolean {
        Log.i("ninja", auth.currentUser.toString())
        return auth.currentUser != null;
    }
}