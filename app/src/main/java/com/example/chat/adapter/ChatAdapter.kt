package com.example.chat.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.models.Message
import com.example.chat.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class ChatAdapter(_list: ArrayList<Message>, _context: Context, _receiverId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val repository: FirebaseRepository = FirebaseRepository(_context)
    private val messages: ArrayList<Message> = _list
    private val context: Context = _context
    private val receiverId: String = _receiverId

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.receiver_text)
        val time: TextView = itemView.findViewById(R.id.receiver_time)
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.sender_text)
        val time: TextView = itemView.findViewById(R.id.sender_time)
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].uId.equals(FirebaseAuth.getInstance().uid)) {
            return SENDER_VIEW_TYPE
        }
        return RECEIVER_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SENDER_VIEW_TYPE) {
            val view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false)
            return SenderViewHolder(view)
        }
        val view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false)
        return ReceiverViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        holder.itemView.setOnLongClickListener {
            if (message.uId == receiverId) {
                return@setOnLongClickListener false
            }
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.deleteMessage(message, receiverId)
                    }
                }).setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                }).show()

            return@setOnLongClickListener false
        }

        if (holder is SenderViewHolder) {
            holder.msg.text = message.message
            val sdf = SimpleDateFormat("h:mm a")
            if (message.timestamp != null) {
                holder.time.text = sdf.format(message.timestamp).toString()
            }
        } else if (holder is ReceiverViewHolder) {
            holder.msg.text = message.message
            val sdf = SimpleDateFormat("h:mm a")
            if (message.timestamp != null) {
                holder.time.text = sdf.format(message.timestamp).toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}