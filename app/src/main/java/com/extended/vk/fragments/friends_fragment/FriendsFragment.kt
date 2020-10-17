package com.extended.vk.fragments.friends_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.extended.vk.R
import com.extended.vk.activity.MainActivity
import com.extended.vk.adapters.ContactsSection
import com.extended.vk.adapters.Friend
import com.extended.vk.adapters.FriendsAdapter
import com.extended.vk.helpers.Account
import com.extended.vk.helpers.Constants
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection

class FriendsFragment : Fragment() {
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var firstProgressLoadingBar: ProgressBar

    var friendsList = ArrayList<Friend>()
    private val adapter = SectionedRecyclerViewAdapter()
    val fragmentName = "Друзья"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFriends()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_friends, container, false)
        friendsRecyclerView = root.findViewById(R.id.friends_recycler_view)
        firstProgressLoadingBar = root.findViewById(R.id.first_loading)

        firstProgressLoadingBar.visibility = View.VISIBLE

        friendsRecyclerView.layoutManager = LinearLayoutManager(activity)
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.friends_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getFriends() {
        val root = activity as MainActivity
        object: Thread() {
             override fun run() = try {
                 val friends = root.api.getFriends(Account.user_id, Constants.user_fields)
                 for (friend in friends) {
                     val etc: String? = when {
                         friend.city != null -> {
                             friend.city
                         }
                         friend.university_name != null -> {
                             friend.university_name
                         }
                         else -> {
                             null
                         }
                     }
                     friendsList.add(Friend(friend.uid, friend.first_name + " " + friend.last_name, friend.photo, etc))
                 }
                 root.runOnUiThread(successRunnable)
             }
             catch (e:Exception) {
                 e.printStackTrace()
             }
        }.start()

    }

    var successRunnable: Runnable = Runnable {
        val importantList = ArrayList<Friend>()
        val anotherList = ArrayList<Friend>()

        friendsList.forEachIndexed { index, friend ->
            if (index in 0..4) {
                importantList.add(friend)
            } else {
                anotherList.add(friend)
            }
        }
        adapter.addSection(ContactsSection("Важные", null, importantList))
        adapter.addSection(ContactsSection("Все друзья", friendsList.size.toString(), anotherList))
        friendsRecyclerView.adapter = adapter
        firstProgressLoadingBar.visibility = View.GONE
    }
}