package com.example.chat.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chat.models.User
import com.example.chat.repository.FirebaseRepository


class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebaseRepository = FirebaseRepository(application)
    var userData: MutableLiveData<User?> = repository.firebaseUserMutableLiveData

    fun updateInfo(obj: HashMap<String, Any>) {
        repository.updateUserInfo(obj)
    }

    fun updateProfilePic(profilePic: Uri?) {
        repository.updateUserProfilePic(profilePic)
    }

    fun getInfo() {
        repository.getUserInfo()
    }

}