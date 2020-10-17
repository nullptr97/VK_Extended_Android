package com.extended.vk.helpers

import android.content.Context
import android.preference.PreferenceManager

object Account {
    var access_token: String? = null
    var user_id: Long = 0
    var is_login: Boolean = false

    fun save(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("access_token", access_token)
        editor.putLong("user_id", user_id)
        editor.putBoolean("is_login", access_token != null)
        editor.apply()
    }
    fun restore(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        access_token = prefs.getString("access_token", null).toString()
        user_id = prefs.getLong("user_id", 0)
        is_login = prefs.getBoolean("is_login", false)
    }
}