package com.example.chat.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chat.R
import com.example.chat.databinding.FragmentActiveChatBinding
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso

class ActiveChatFragment : Fragment(R.layout.fragment_active_chat) {

    private var fragmentActiveChatBinding: FragmentActiveChatBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActiveChatBinding.bind(view)
        fragmentActiveChatBinding = binding

        arguments?.let {
            val args = ActiveChatFragmentArgs.fromBundle(it)
            fragmentActiveChatBinding!!.chatUsername.text = args.userName
            Picasso.get().load(args.profilePic).placeholder(R.drawable.avatar4).into(
                fragmentActiveChatBinding!!.chatProfileImage
            )
        }

        fragmentActiveChatBinding?.btnBack?.setOnClickListener {
            findNavController().navigate(ActiveChatFragmentDirections.actionActiveChatFragmentToMainFragment())
        }
    }
}