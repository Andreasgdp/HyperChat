package com.example.chat.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chat.models.Message
import com.example.chat.models.User
import com.example.chat.repository.FirebaseRepository


class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FirebaseRepository = FirebaseRepository(application)
    var chatsData: MutableLiveData<ArrayList<User>> = repository.firebaseChatsMutableLiveData
    var messagesData: MutableLiveData<ArrayList<Message>> =
        repository.firebaseChatMessagesMutableLiveData

    fun updateChatsList(list: ArrayList<User>) {
        repository.updateChatsList(list)
    }

    fun loadChatMessages(messages: ArrayList<Message>, senderRoom: String) {
        repository.loadChatMessages(messages, senderRoom)
    }

    fun sendMessage(
        model: Message,
        senderRoom: String,
        receiverRoom: String
    ) {
        repository.sendMessage(model, senderRoom, receiverRoom)
    }

}