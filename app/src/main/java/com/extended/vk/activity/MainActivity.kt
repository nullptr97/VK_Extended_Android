package com.extended.vk.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.extended.vk.R
import com.extended.vk.api.Api
import com.extended.vk.fragments.friends_fragment.FriendsFragment
import com.extended.vk.fragments.menu_fragment.MenuFragment
import com.extended.vk.fragments.messages_fragment.MessagesFragment
import com.extended.vk.fragments.news_fragment.NewsFragment
import com.extended.vk.fragments.profile_fragment.ProfileFragment
import com.extended.vk.helpers.Account
import com.extended.vk.helpers.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val REQUEST_LOGIN = 1
    var account = Account
    lateinit var api: Api
    lateinit var toolbarTextView: TextView
    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initApi()
        setContentView(R.layout.activity_main)

        navigation = findViewById(R.id.nav_view)
        toolbarTextView = findViewById(R.id.toolbar_title)

        setSupportActionBar(toolbar)

        navigation.setOnNavigationItemSelectedListener(this)
        setupToolbar()

        if (account.is_login) navigation.selectedItemId = R.id.navigation_friends; navigation.performClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                //авторизовались успешно
                account.access_token = data?.getStringExtra("token")
                account.user_id = data?.getLongExtra("user_id", 0) ?: 0
                account.is_login = data?.getBooleanExtra("is_login", false) ?: false
                account.save(this@MainActivity)
                api = Api(account.access_token, Constants.clientAppId)

                navigation.selectedItemId = R.id.navigation_friends
                navigation.performClick()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_news ->  {
                val fragment = NewsFragment()
                changeFragment(fragment, fragment::class.java.name)
                setupToolbar(fragment.fragmentName)
            }
            R.id.navigation_menu -> {
                val fragment = MenuFragment()
                changeFragment(fragment, fragment::class.java.name)
                setupToolbar(fragment.fragmentName)
            }
            R.id.navigation_messages -> {
                val fragment = MessagesFragment()
                changeFragment(fragment, fragment::class.java.name)
                setupToolbar(fragment.fragmentName)
            }
            R.id.navigation_friends -> {
                val fragment = FriendsFragment()
                changeFragment(fragment, fragment::class.java.name)
                setupToolbar(fragment.fragmentName)
            }
            R.id.navigation_profile -> {
                val fragment = ProfileFragment()
                changeFragment(fragment, fragment::class.java.name)
                setupToolbar(fragment.fragmentName)
            }
        }
        return true
    }

    private fun changeFragment(fragment: Fragment, tagFragmentName: String) {
        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }
        var fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName)
        } else {
            fragmentTransaction.show(fragmentTemp)
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    open fun setupToolbar(title: CharSequence = "Новости") {
        toolbarTextView.text = title
    }

    private fun initApi() {
        account.restore(this)

        if (account.is_login) {
            api = Api(account.access_token, Constants.clientAppId)
            return
        }
        startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN)
    }
}