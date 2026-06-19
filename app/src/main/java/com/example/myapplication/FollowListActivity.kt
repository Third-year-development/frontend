package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class FollowListActivity : BaseActivity() {
    private lateinit var followListRecycle: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_follow_list)
        setupToolbar()

        val listType = intent.getStringExtra("listType") ?: "follow"

        supportActionBar?.title = if (listType == "follower")
            getString(R.string.follow_list_title_follower)
        else
            getString(R.string.follow_list_title_follow)

        followListRecycle = findViewById(R.id.followListRecycle)
        followListRecycle.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // バックエンドは /following / /followers （自分のフォロー情報のみ）
        // targetUserId は現在未使用（自分のリストを表示）
        val endpoint = if (listType == "follower") Constants.ENDPOINT_FOLLOWERS else Constants.ENDPOINT_FOLLOWING
        loadList(endpoint)
    }

    private fun loadList(endpoint: String) {
        ApiClient.get(this, endpoint, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@FollowListActivity, "読み込みエラー", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                // レスポンス: {"user_line": [...]}
                val jsonArray: JSONArray = when {
                    root.has("user_line") -> root.getJSONArray("user_line")
                    else -> JSONArray(body)
                }
                val list = mutableListOf<UserRowData>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(UserRowData(
                        userName = obj.optString("name", ""),
                        followCount = obj.optInt("follows_count", 0),
                        followerCount = obj.optInt("followers_count", 0),
                        userImageResId = R.mipmap.ic_launcher
                    ))
                }
                runOnUiThread {
                    followListRecycle.adapter = UserAdapter(list)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@FollowListActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
