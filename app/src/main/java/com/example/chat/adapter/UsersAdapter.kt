package com.example.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.models.User
import com.squareup.picasso.Picasso

class UsersAdapter(_list: ArrayList<User>, _context: Context): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val list: ArrayList<User> = _list
    private val context: Context = _context

    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val image: ImageView = itemView.findViewById(R.id.list_profile_image)
        public val userName: TextView = itemView.findViewById(R.id.list_user_name)
        public val lastMessage: TextView = itemView.findViewById(R.id.list_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        Picasso.get().load(user.profilePic).placeholder((R.drawable.avatar4)).into(holder.image)
        holder.userName.text = user.userName
    }

    override fun getItemCount(): Int {
        return list.size
    }
}