package com.extended.vk.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.extended.vk.R
import com.squareup.picasso.Picasso
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class Friend(val id: Long, val name: String, val imageUrl: String, val etc: String? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val friend = other as Friend
        if (id != friend.id) return false
        if (name != friend.name) return false
        if (etc != friend.etc) return false
        return imageUrl == friend.imageUrl
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + etc.hashCode()
        return result
    }
}

class FriendsAdapter: RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    private val friendsList = ArrayList<Friend>()

    inner class FriendsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.user_image_view)
        val nameTextView: TextView = itemView.findViewById(R.id.user_name_text_view)
        val etcTextView: TextView = itemView.findViewById(R.id.etc_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list_item, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        friendsList[position].let { friend ->
            holder.nameTextView.text = friend.name
            Picasso.with(holder.itemView.context).load(friend.imageUrl).into(holder.userImageView)
            if (friend.etc != null && friend.etc.isNotEmpty()) {
                holder.etcTextView.visibility = VISIBLE
                holder.etcTextView.text = friend.etc
            } else {
                holder.nameTextView.setPadding(0, 0 ,0 ,0)
                holder.etcTextView.visibility = GONE
            }
        }
    }

    override fun getItemCount():Int {
        return friendsList.size
    }

    fun setItems(tweets: Collection<Friend>) {
        friendsList.addAll(tweets)
        notifyDataSetChanged()
    }
    fun clearItems() {
        friendsList.clear()
        notifyDataSetChanged()
    }
}

internal class ContactsSection(private val title: String, private val count: String? = null, @NonNull private val list: List<Friend>):
    Section(SectionParameters.builder().itemResourceId(R.layout.friend_list_item).headerResourceId(R.layout.section_header).build()) {

    inner class FriendsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.user_image_view)
        val nameTextView: TextView = itemView.findViewById(R.id.user_name_text_view)
        val etcTextView: TextView = itemView.findViewById(R.id.etc_text_view)
    }

    override fun getItemViewHolder(view: View):RecyclerView.ViewHolder {
        return FriendsViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as FriendsViewHolder
        list[position].let { friend ->
            holder.nameTextView.text = friend.name
            Picasso.with(holder.itemView.context).load(friend.imageUrl).into(holder.userImageView)
            if (friend.etc != null && friend.etc.isNotEmpty()) {
                holder.etcTextView.visibility = VISIBLE
                holder.etcTextView.text = friend.etc
            } else {
                holder.nameTextView.setPadding(0, 0 ,0 ,0)
                holder.etcTextView.visibility = GONE
            }
        }
    }

    override fun getHeaderViewHolder(view:View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        holder as HeaderViewHolder
        holder.headerTextView.text = title
        holder.headerCountTextView.text = count
        if (list.size == 5) {
            holder.headerSortTypeTextView.visibility = GONE
            holder.headerSortTypeDescriptionTextView.visibility = GONE
        } else {
            holder.headerSortTypeTextView.visibility = VISIBLE
            holder.headerSortTypeDescriptionTextView.visibility = VISIBLE
        }
    }

    override fun getContentItemsTotal(): Int {
        return list.size
    }

    internal interface ClickListener {
        fun onItemRootViewClicked(@NonNull section:ContactsSection, itemAdapterPosition:Int)
    }
}

internal class HeaderViewHolder(@NonNull view:View):RecyclerView.ViewHolder(view) {
    val headerTextView: TextView = view.findViewById(R.id.header_text_view)
    val headerCountTextView: TextView = view.findViewById(R.id.header_count_text_view)
    val headerSortTypeTextView: TextView = view.findViewById(R.id.header_sort_type_text_view)
    val headerSortTypeDescriptionTextView: TextView = view.findViewById(R.id.header_sort_type_description_text)
}