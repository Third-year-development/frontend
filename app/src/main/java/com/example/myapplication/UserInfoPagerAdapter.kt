package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class UserInfoPagerAdapter(
    activity: FragmentActivity,
    private val targetUserId: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_WHISPERS)
        1 -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_FOLLOWING)
        2 -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_FOLLOWERS)
        else -> UserInfoTabFragment.newInstance(targetUserId, UserInfoTabFragment.TAB_LIKES)
    }
}
