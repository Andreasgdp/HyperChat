package com.example.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.models.Message
import com.example.chat.models.User
import com.example.chat.views.MainFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UsersAdapter(_list: ArrayList<User>, _context: Context) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val list: ArrayList<User> = _list
    private val context: Context = _context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.list_profile_image)
        val userName: TextView = itemView.findViewById(R.id.list_user_name)
        val lastMessage: TextView = itemView.findViewById(R.id.list_last_message)
        val lastMessageDate: TextView = itemView.findViewById(R.id.list_last_message_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        Picasso.get().load(user.profilePic).placeholder((R.drawable.avatar4)).into(holder.image)
        holder.userName.text = user.userName


        val database =
            Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
        val chatsRef = database.getReference("Chats")
        chatsRef.child(FirebaseAuth.getInstance().uid + user.userId)
            .orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    var model = Message();
                    children.forEach {
                        println(it.toString())
                        model = it.getValue(Message::class.java)!!;
                    }
                    holder.lastMessage.text = model.message
                    holder.lastMessageDate.text = model.timestamp
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        holder.itemView.setOnClickListener { view ->
            findNavController(view).navigate(
                MainFragmentDirections.actionMainFragmentToActiveChatFragment(
                    user.userId.toString(),
                    user.userName.toString(),
                    user.profilePic.toString()
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}