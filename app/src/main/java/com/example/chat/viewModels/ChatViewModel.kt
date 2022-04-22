package com.example.chat.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat.models.Message
import com.example.chat.models.User
import com.example.chat.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebaseRepository = FirebaseRepository(application)
    var chatsData: MutableLiveData<ArrayList<User>> = repository.firebaseChatsMutableLiveData
    var messagesData: MutableLiveData<ArrayList<Message>> =
        repository.firebaseChatMessagesMutableLiveData

    fun updateChatsList(list: ArrayList<User>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChatsList(list)
        }
    }

    fun loadChatMessages(messages: ArrayList<Message>, senderRoom: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.listenToChatMessages(messages, senderRoom)
        }
    }

    fun sendMessage(
        model: Message,
        senderRoom: String,
        receiverRoom: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendMessage(model, senderRoom, receiverRoom)
        }
    }

}