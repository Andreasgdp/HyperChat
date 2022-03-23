package com.example.chat.subViews.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.UsersAdapter
import com.example.chat.databinding.FragmentChatsBinding
import com.example.chat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatsFragment : Fragment() {

    private var fragmentChatsBinding: FragmentChatsBinding? = null
    private val list: ArrayList<User> = ArrayList()
    private val database =
        Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("Users")


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
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentChatsBinding = null
    }

}
