package com.extended.vk.fragments.menu_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.extended.vk.R
import com.extended.vk.activity.MainActivity

class MenuFragment : Fragment() {
    val fragmentName = "Меню"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_menu, container, false)
        return root
    }
}