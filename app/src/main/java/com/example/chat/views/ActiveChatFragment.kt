package com.example.chat.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.ChatAdapter
import com.example.chat.databinding.FragmentActiveChatBinding
import com.example.chat.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ActiveChatFragment : Fragment(R.layout.fragment_active_chat) {

    private var fragmentActiveChatBinding: FragmentActiveChatBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActiveChatBinding.bind(view)
        fragmentActiveChatBinding = binding
        val auth = FirebaseAuth.getInstance()
        val database =
            Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
        val chatsRef = database.getReference("Chats")

        val senderId = auth.uid
        var receiverId: String = ""
        var userName: String = ""
        var profilePic: String = ""

        arguments?.let {
            val args = ActiveChatFragmentArgs.fromBundle(it)
            receiverId = args.userId
            userName = args.userName
            profilePic = args.profilePic

            fragmentActiveChatBinding!!.chatUsername.text = userName
            Picasso.get().load(profilePic).placeholder(R.drawable.avatar4).into(
                fragmentActiveChatBinding!!.chatProfileImage
            )
        }

        fragmentActiveChatBinding?.btnBack?.setOnClickListener {
            findNavController().navigate(ActiveChatFragmentDirections.actionActiveChatFragmentToMainFragment())
        }


        val messages = ArrayList<Message>()
        val chatAdapter = context?.let { ChatAdapter(messages, it, receiverId) }
        binding.chatRecyclerView.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(context)
        fragmentActiveChatBinding!!.chatRecyclerView.layoutManager = layoutManager

        val senderRoom = senderId + receiverId
        val receiverRoom = receiverId + senderId

        chatsRef.child(senderRoom).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages.clear()
                for (snapshot in dataSnapshot.children) {
                    val model: Message? = snapshot.getValue(Message::class.java)
                    model!!.messageId = snapshot.key
                    messages.add(model)
                }
                chatAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ninja", "Failed to read value.", error.toException())
            }
        })

        fragmentActiveChatBinding!!.btnSend.setOnClickListener {
            val message = binding.enterMessage.text.toString()
            if (message.isEmpty()) {
                return@setOnClickListener
            }
            val sdf = SimpleDateFormat("hh:mm")
            val model =
                Message(uId = senderId, message = message, timestamp = sdf.format(Date()))
            binding.enterMessage.setText("")

            chatsRef.child(senderRoom).push().setValue(model).addOnSuccessListener {
                chatsRef.child(receiverRoom).push().setValue(model).addOnSuccessListener {
                    Toast.makeText(context, "Message send!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}