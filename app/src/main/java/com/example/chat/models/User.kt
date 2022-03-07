package com.example.chat.models

data class User(
    var userName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var profilePic: String? = null,
    var userId: String? = null,
    var lastMessage: String? = null,
    var status: String? = null
) {

}