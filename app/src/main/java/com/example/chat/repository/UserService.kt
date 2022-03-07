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

    interface CreateUserCallback {
        fun onSignUpSuccess(user: User)
        fun onSignUpFail(exception: String?)
        fun onTest(test: String?)
        fun onSignUpDone()
    }

    fun createUser(
        username: String,
        email: String,
        password: String,
        callbackCreate: CreateUserCallback
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(username, email, password);

                // make sure that uid is available at use.
                task.result?.user?.uid?.let { uid ->
                    usersRef.child(uid).setValue(user)
                    user.userId = uid;
                }

                callbackCreate.onSignUpSuccess(user);
            } else {
                callbackCreate.onSignUpFail(task.exception.toString());
            }

            callbackCreate.onSignUpDone();
        };
    }
}