package com.example.chat.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.chat.R
import com.example.chat.adapter.UsersAdapter
import com.example.chat.models.Message
import com.example.chat.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class FirebaseRepository(private val applicationContext: Context) {
    val firebaseUserMutableLiveData: MutableLiveData<User?> = MutableLiveData()
    val firebaseChatsMutableLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    val firebaseChatMessagesMutableLiveData: MutableLiveData<ArrayList<Message>> = MutableLiveData()
    val userLoggedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val authDoneMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database =
        Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")
    private val chatsRef = database.getReference("Chats")
    private val storage = FirebaseStorage.getInstance()
    var googleSignInClient: GoogleSignInClient

    init {
        if (auth.currentUser != null) {
            val currUser = User(email = auth.currentUser!!.email)
            firebaseUserMutableLiveData.postValue(currUser)
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
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
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT)
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
                Toast.makeText(applicationContext, signIn.exception.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        userLoggedOutMutableLiveData.postValue(true)
    }

    fun updateUserInfo(obj: HashMap<String, Any>) {
        usersRef.child(auth.uid!!).updateChildren(obj)
    }

    fun updateUserProfilePic(profilePic: Uri?) {
        val reference = storage.reference.child("profile_pic").child(
            auth.currentUser!!.uid
        )

        reference.putFile(profilePic!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener {
                usersRef.child(FirebaseAuth.getInstance().uid!!)
                    .child("profilePic").setValue(it.toString())
            }
        }
    }

    fun getUserInfo() {
        usersRef.child(auth.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currUser = snapshot.getValue(User::class.java)
                    firebaseUserMutableLiveData.postValue(currUser)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("ninja", "Failed to read value.", error.toException())
                }

            })
    }

    fun checkSignedIn() {
        if (auth.currentUser != null) {
            val currUser = User(email = auth.currentUser?.email)
            firebaseUserMutableLiveData.postValue(currUser)
        } else {
            firebaseUserMutableLiveData.postValue(null)
        }
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
                        Toast.makeText(
                            applicationContext,
                            signIn.exception.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } catch (e: ApiException) {
        }
    }

    fun deleteMessage(message: Message, receiverId: String) {
        val senderRoom = FirebaseAuth.getInstance().uid + receiverId
        message.messageId?.let { messageId ->
            chatsRef.child(senderRoom).child(messageId).setValue(null).addOnSuccessListener {
                Toast.makeText(applicationContext, "shits happening!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun sendMessage(
        model: Message,
        senderRoom: String,
        receiverRoom: String
    ) {
        chatsRef.child(senderRoom).push().setValue(model).addOnSuccessListener {
            chatsRef.child(receiverRoom).push().setValue(model)
        }
    }

    fun updateLastMessage(
        user: User,
        holder: UsersAdapter.ViewHolder
    ) {
        chatsRef.child(FirebaseAuth.getInstance().uid + user.userId)
            .orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    var model = Message()
                    children.forEach {
                        println(it.toString())
                        model = it.getValue(Message::class.java)!!
                    }
                    holder.lastMessage.text = model.message
                    holder.lastMessageDate.text = model.timestamp
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun updateChatsList(list: ArrayList<User>) {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapShot in dataSnapshot.children) {
                    val user: User? = snapShot.getValue(User::class.java)
                    user!!.userId = snapShot.key

                    // Use this method to remove the users from view, that is not connected with you
                    if (!user.userId.equals(FirebaseAuth.getInstance().uid)) {
                        list.add(user)
                    }
                }
                firebaseChatsMutableLiveData.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ninja", "Failed to read value.", error.toException())
            }
        })
    }

    fun loadChatMessages(messages: ArrayList<Message>, senderRoom: String) {
        chatsRef.child(senderRoom).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages.clear()
                for (snapshot in dataSnapshot.children.reversed()) {
                    val model: Message? = snapshot.getValue(Message::class.java)
                    model!!.messageId = snapshot.key
                    messages.add(model)
                }
                firebaseChatMessagesMutableLiveData.postValue(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ninja", "Failed to read value.", error.toException())
            }
        })
    }
}