package com.extended.vk.fragments.news_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.extended.vk.R
import com.extended.vk.activity.MainActivity

class NewsFragment : Fragment() {
    val fragmentName = "Новости"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        return root
    }
}