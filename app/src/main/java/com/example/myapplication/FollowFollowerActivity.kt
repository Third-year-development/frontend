package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FollowFollowerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_follow_follower)
        setupToolbar()

        val targetUserId = intent.getStringExtra("userId") ?: run { finish(); return }
        val startTab = intent.getIntExtra("startTab", 0)  // 0=フォロー, 1=フォロワー
        val targetUserName = intent.getStringExtra("userName") ?: ""

        supportActionBar?.title = targetUserName

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = object : FragmentStateAdapter(this as FragmentActivity) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_FOLLOWING)
                else -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_FOLLOWERS)
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.user_info_follow)
                       else getString(R.string.user_info_follower_label)
        }.attach()

        viewPager.currentItem = startTab
    }
}
