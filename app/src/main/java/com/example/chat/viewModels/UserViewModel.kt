package com.example.chat.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat.models.User
import com.example.chat.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebaseRepository = FirebaseRepository(application)
    var userData: MutableLiveData<User?> = repository.firebaseUserMutableLiveData

    fun updateInfo(obj: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserInfo(obj)
        }
    }

    fun updateProfilePic(profilePic: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserProfilePic(profilePic)
        }
    }

    fun getInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserInfo()
        }
    }

}