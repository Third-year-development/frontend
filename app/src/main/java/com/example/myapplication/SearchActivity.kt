package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SearchActivity : BaseActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var userRadio: RadioButton
    private lateinit var whisperRadio: RadioButton
    private lateinit var searchEdit: EditText
    private lateinit var searchButton: Button
    private lateinit var searchRecycle: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var loginUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        setupToolbar()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        loginUserId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""

        radioGroup = findViewById(R.id.radioGroup)
        userRadio = findViewById(R.id.userRadio)
        whisperRadio = findViewById(R.id.whisperRadio)
        searchEdit = findViewById(R.id.searchEdit)
        searchButton = findViewById(R.id.searchButton)
        searchRecycle = findViewById(R.id.searchRecycle)
        emptyText = findViewById(R.id.searchEmptyText)

        searchRecycle.layoutManager = LinearLayoutManager(this)
        searchRecycle.visibility = View.GONE
        emptyText.visibility = View.GONE

        searchButton.setOnClickListener { executeSearch() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun executeSearch() {
        val query = searchEdit.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, getString(R.string.search_query_required), Toast.LENGTH_SHORT).show()
            return
        }

        when (radioGroup.checkedRadioButtonId) {
            R.id.userRadio -> {
                // GET /api/v1/search/users/{keyword} → {"user_line": [...]}
                ApiClient.get(this, "${Constants.ENDPOINT_SEARCH_USERS}/$query", object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            runOnUiThread { Toast.makeText(this@SearchActivity, "検索エラー", Toast.LENGTH_SHORT).show() }
                            return
                        }
                        val body = response.body?.string() ?: return
                        val root = JSONObject(body)
                        val jsonArray: JSONArray = when {
                            root.has("user_line") -> root.getJSONArray("user_line")
                            else -> JSONArray(body)
                        }
                        val userList = mutableListOf<UserRowData>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            userList.add(UserRowData(
                                userName = obj.optString("name", ""),
                                followCount = obj.optInt("follows_count", 0),
                                followerCount = obj.optInt("followers_count", 0),
                                userImageResId = R.mipmap.ic_launcher
                            ))
                        }
                        runOnUiThread {
                            if (userList.isEmpty()) {
                                searchRecycle.visibility = View.GONE
                                emptyText.visibility = View.VISIBLE
                            } else {
                                emptyText.visibility = View.GONE
                                searchRecycle.adapter = UserAdapter(userList)
                                searchRecycle.visibility = View.VISIBLE
                            }
                        }
                    }
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread { Toast.makeText(this@SearchActivity, "通信エラー", Toast.LENGTH_SHORT).show() }
                    }
                })
            }

            R.id.whisperRadio -> {
                // GET /api/v1/search/whispers/{keyword} → {"whisper": [...]}
                ApiClient.get(this, "${Constants.ENDPOINT_SEARCH_WHISPERS}/$query", object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            runOnUiThread { Toast.makeText(this@SearchActivity, "検索エラー", Toast.LENGTH_SHORT).show() }
                            return
                        }
                        val body = response.body?.string() ?: return
                        val root = JSONObject(body)
                        val jsonArray = root.optJSONArray("whisper") ?: JSONArray()
                        val whisperList = TimelineActivity.parseWhispers(jsonArray)
                        runOnUiThread {
                            if (whisperList.isEmpty()) {
                                searchRecycle.visibility = View.GONE
                                emptyText.visibility = View.VISIBLE
                            } else {
                                emptyText.visibility = View.GONE
                                searchRecycle.adapter = WhisperAdapter(
                                    whisperList, loginUserId,
                                    onLikeClick = {},
                                    onUserClick = { item ->
                                        startActivity(android.content.Intent(this@SearchActivity, UserInfoActivity::class.java).apply {
                                            putExtra("userId", item.userId)
                                        })
                                    },
                                    onWhisperClick = { item ->
                                        startActivity(item.toDetailIntent(this@SearchActivity))
                                    }
                                )
                                searchRecycle.visibility = View.VISIBLE
                            }
                        }
                    }
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread { Toast.makeText(this@SearchActivity, "通信エラー", Toast.LENGTH_SHORT).show() }
                    }
                })
            }

            else -> {
                Toast.makeText(this, getString(R.string.search_type_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
