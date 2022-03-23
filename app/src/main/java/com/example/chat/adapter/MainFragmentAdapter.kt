package com.example.chat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chat.subViews.mainFragment.BefriendFragment
import com.example.chat.subViews.mainFragment.ChatsFragment

class MainFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChatsFragment()
            }
            1 -> {
                BefriendFragment()
            }
            else -> {
                ChatsFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        val title = when (position) {
            0 -> {
                "CHATS"
            }
            1 -> {
                "Connect"
            }
            else -> {
                "CHATS"
            }
        }

        return title
    }
}