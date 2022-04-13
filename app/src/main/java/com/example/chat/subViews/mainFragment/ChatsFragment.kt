package com.example.chat.subViews.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.UsersAdapter
import com.example.chat.databinding.FragmentChatsBinding
import com.example.chat.models.User
import com.example.chat.viewModels.ChatViewModel


class ChatsFragment : Fragment() {

    private var fragmentChatsBinding: FragmentChatsBinding? = null
    private var list: ArrayList<User> = ArrayList()
    private val viewModel: ChatViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChatsBinding.bind(view)
        fragmentChatsBinding = binding

        val adapter = context?.let { UsersAdapter(list, it) }
        fragmentChatsBinding!!.chatRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        fragmentChatsBinding!!.chatRecyclerView.layoutManager = layoutManager

        viewModel.updateChatsList(list)
        viewModel.chatsData.observe(viewLifecycleOwner) {
            list = it
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentChatsBinding = null
    }

}
