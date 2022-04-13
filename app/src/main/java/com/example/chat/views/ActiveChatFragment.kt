package com.example.chat.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.ChatAdapter
import com.example.chat.databinding.FragmentActiveChatBinding
import com.example.chat.models.Message
import com.example.chat.viewModels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ActiveChatFragment : Fragment(R.layout.fragment_active_chat) {

    private var fragmentActiveChatBinding: FragmentActiveChatBinding? = null
    private val viewModel: ChatViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActiveChatBinding.bind(view)
        fragmentActiveChatBinding = binding

        val senderId = FirebaseAuth.getInstance().uid
        var receiverId = ""
        var userName = ""
        var profilePic = ""

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


        var messages = ArrayList<Message>()
        val chatAdapter = context?.let { ChatAdapter(messages, it, receiverId) }
        binding.chatRecyclerView.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        fragmentActiveChatBinding!!.chatRecyclerView.layoutManager = layoutManager

        val senderRoom = senderId + receiverId
        val receiverRoom = receiverId + senderId

        viewModel.loadChatMessages(messages, senderRoom)

        viewModel.messagesData.observe(viewLifecycleOwner) {
            messages = it
            chatAdapter!!.notifyDataSetChanged()
        }

        fragmentActiveChatBinding!!.btnSend.setOnClickListener {
            val message = binding.enterMessage.text.toString()
            if (message.isEmpty()) {
                return@setOnClickListener
            }
            val sdf = SimpleDateFormat("h:mm a")
            val model =
                Message(uId = senderId, message = message, timestamp = sdf.format(Date()))
            binding.enterMessage.setText("")

            viewModel.sendMessage(model, senderRoom, receiverRoom)
        }
    }
}