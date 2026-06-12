package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class UserInfoActivity : BaseActivity() {
    private lateinit var userNameText: TextView
    private lateinit var userIdText: TextView
    private lateinit var profileText: TextView
    private lateinit var followCntText: TextView
    private lateinit var followerCntText: TextView
    private lateinit var followButton: Button
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var targetUserId: String
    private lateinit var loginUserId: String
    private var isFollowing = false

    private var followCount = 0
    private var followerCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_info)
        setupToolbar()

        targetUserId = intent.getStringExtra("userId") ?: run { finish(); return }

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        loginUserId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""

        userNameText = findViewById(R.id.userNameText)
        userIdText = findViewById(R.id.userIdText)
        profileText = findViewById(R.id.profileText)
        followCntText = findViewById(R.id.followCntText)
        followerCntText = findViewById(R.id.followerCntText)
        followButton = findViewById(R.id.followButton)
        tabLayout = findViewById(R.id.userInfoTabLayout)
        viewPager = findViewById(R.id.userInfoViewPager)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        followButton.setOnClickListener { toggleFollow() }

        // フォロー数・フォロワー数タップ → フォロー/フォロワー一覧画面
        val followClickListener = android.view.View.OnClickListener {
            startActivity(Intent(this, FollowFollowerActivity::class.java).apply {
                putExtra("userId", targetUserId)
                putExtra("startTab", 0)
                putExtra("userName", userNameText.text.toString())
            })
        }
        val followerClickListener = android.view.View.OnClickListener {
            startActivity(Intent(this, FollowFollowerActivity::class.java).apply {
                putExtra("userId", targetUserId)
                putExtra("startTab", 1)
                putExtra("userName", userNameText.text.toString())
            })
        }
        followCntText.setOnClickListener(followClickListener)
        findViewById<android.widget.TextView>(R.id.followLabel).setOnClickListener(followClickListener)
        followerCntText.setOnClickListener(followerClickListener)
        findViewById<android.widget.TextView>(R.id.followerLabel).setOnClickListener(followerClickListener)

        viewPager.adapter = UserInfoPagerAdapter(this, targetUserId)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.user_info_tab_whispers)
                1 -> getString(R.string.user_info_tab_follow, followCount)
                2 -> getString(R.string.user_info_tab_follower, followerCount)
                else -> getString(R.string.user_info_tab_likes)
            }
        }.attach()

        loadUserInfo()
    }

    private fun loadUserInfo() {
        ApiClient.get(this, "${Constants.ENDPOINT_GET_USER}/$targetUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val user = root.optJSONObject("userprofile") ?: root
                followCount = user.optInt("follows_count", 0)
                followerCount = user.optInt("followers_count", 0)
                isFollowing = root.optBoolean("is_following", false)

                runOnUiThread {
                    userNameText.text = user.optString("name", "")
                    userIdText.text = user.optString("email", "")
                    val profile = user.optJSONObject("profile")
                    profileText.text = profile?.optString("profile", "") ?: ""
                    followCntText.text = followCount.toString()
                    followerCntText.text = followerCount.toString()

                    // 他ユーザの場合のみフォローボタンを表示
                    if (targetUserId != loginUserId) {
                        followButton.visibility = View.VISIBLE
                        updateFollowButtonState()
                    }

                    // タブラベルにカウントを反映
                    tabLayout.getTabAt(1)?.text = getString(R.string.user_info_tab_follow, followCount)
                    tabLayout.getTabAt(2)?.text = getString(R.string.user_info_tab_follower, followerCount)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UserInfoActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateFollowButtonState() {
        followButton.text = if (isFollowing)
            getString(R.string.user_info_unfollow_button)
        else
            getString(R.string.user_info_follow_button)
    }

    private fun toggleFollow() {
        val newFlag = !isFollowing
        val json = JSONObject().apply {
            put("follow_user_id", targetUserId.toIntOrNull() ?: 0)
            put("following", newFlag)
        }
        ApiClient.post(this, Constants.ENDPOINT_FOLLOW, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                isFollowing = newFlag
                runOnUiThread { updateFollowButtonState() }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}
