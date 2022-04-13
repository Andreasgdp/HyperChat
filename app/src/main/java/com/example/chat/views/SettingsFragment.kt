package com.example.chat.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentSettingsBinding
import com.example.chat.models.User
import com.example.chat.viewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var fragmentSettingsBinding: FragmentSettingsBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)
        fragmentSettingsBinding = binding

        auth = FirebaseAuth.getInstance()
        database =
            Firebase.database("https://hyperchat-282b7-default-rtdb.europe-west1.firebasedatabase.app/")
        storage = FirebaseStorage.getInstance()

        binding.btnSettingsBack.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToMainFragment()
            )
        }

        binding.btnSave.setOnClickListener {
            if (!binding.inputAbout.text.toString().equals("") || !binding.inputUsernameChange.text.toString().equals("")) {
                val about = binding.inputAbout.text.toString()
                val username = binding.inputUsernameChange.text.toString()

                val obj: HashMap<String, Any> = hashMapOf()

                obj["userName"] = username
                obj["status"] = about

                database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!).updateChildren(obj)
            } else {
                Toast.makeText(activity, "Enter Credentials!", Toast.LENGTH_SHORT).show()
            }
        }

        database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.profilePic).placeholder(R.drawable.avatar4)
                        .into(binding.listProfileImage3)

                    binding.inputAbout.setText(user.status)
                    binding.inputUsernameChange.setText(user.userName)
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        binding.profilePicAddBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data!!.data != null) {
            val sFile = data.data
            fragmentSettingsBinding!!.listProfileImage3.setImageURI(sFile)

            val reference =
                FirebaseAuth.getInstance().uid?.let { uid ->
                    storage.reference.child("profile_pic").child(
                        uid
                    )
                }
            reference!!.putFile(sFile!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener {
                    database.reference.child("Users").child(FirebaseAuth.getInstance().uid!!)
                        .child("profilePic").setValue(it.toString())
                }
            }
        }


    }
}