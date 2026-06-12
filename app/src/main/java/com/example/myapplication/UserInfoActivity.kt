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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var userWhisperRecycle: RecyclerView

    private lateinit var targetUserId: String
    private lateinit var loginUserId: String
    private var isFollowing = false

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
        userWhisperRecycle = findViewById(R.id.userWhisperRecycle)

        userWhisperRecycle.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 自分のプロフィールならフォローボタン非表示
        if (targetUserId == loginUserId) followButton.visibility = View.GONE

        followCntText.setOnClickListener {
            startActivity(Intent(this, FollowListActivity::class.java).apply {
                putExtra("userId", targetUserId)
                putExtra("listType", "follow")
            })
        }
        followerCntText.setOnClickListener {
            startActivity(Intent(this, FollowListActivity::class.java).apply {
                putExtra("userId", targetUserId)
                putExtra("listType", "follower")
            })
        }

        followButton.setOnClickListener { toggleFollow() }

        loadUserInfo()
        loadUserWhispers()
    }

    private fun loadUserInfo() {
        // GET /api/v1/users/{id} → {"userprofile": {...}}
        ApiClient.get(this, "${Constants.ENDPOINT_GET_USER}/$targetUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val user = root.optJSONObject("userprofile") ?: root
                runOnUiThread {
                    userNameText.text = user.optString("name", "")
                    userIdText.text = user.optString("email", "")
                    val profile = user.optJSONObject("profile")
                    profileText.text = profile?.optString("profile", "") ?: ""
                    followCntText.text = user.optInt("follows_count", 0).toString()
                    followerCntText.text = user.optInt("followers_count", 0).toString()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UserInfoActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadUserWhispers() {
        // GET /api/v1/user/whispers/{id} → {"user_line": {...}, "whisper": [...]}
        ApiClient.get(this, "${Constants.ENDPOINT_USER_WHISPERS}/$targetUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val jsonArray = root.optJSONArray("whisper") ?: return
                val list = TimelineActivity.parseWhispers(jsonArray)
                runOnUiThread {
                    userWhisperRecycle.adapter = WhisperAdapter(
                        list, loginUserId,
                        onLikeClick = {},
                        onUserClick = { item ->
                            startActivity(Intent(this@UserInfoActivity, UserInfoActivity::class.java).apply {
                                putExtra("userId", item.userId)
                            })
                        },
                        onWhisperClick = { item ->
                            startActivity(item.toDetailIntent(this@UserInfoActivity))
                        }
                    )
                }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    private fun toggleFollow() {
        val newFlag = !isFollowing
        // POST /api/v1/followcheck → {follow_user_id, following}
        val json = JSONObject().apply {
            put("follow_user_id", targetUserId.toIntOrNull() ?: 0)
            put("following", newFlag)
        }
        ApiClient.post(this, Constants.ENDPOINT_FOLLOW, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                isFollowing = newFlag
                runOnUiThread {
                    followButton.text = if (isFollowing)
                        getString(R.string.user_info_unfollow_button)
                    else
                        getString(R.string.user_info_follow_button)
                }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}
