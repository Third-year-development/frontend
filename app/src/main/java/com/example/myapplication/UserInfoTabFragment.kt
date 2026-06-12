package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class UserInfoTabFragment : Fragment() {

    companion object {
        const val TAB_WHISPERS = 0
        const val TAB_FOLLOWING = 1
        const val TAB_FOLLOWERS = 2
        const val TAB_LIKES = 3

        private const val ARG_USER_ID = "userId"
        private const val ARG_TAB = "tab"

        fun newInstance(userId: String, tab: Int) = UserInfoTabFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_ID, userId)
                putInt(ARG_TAB, tab)
            }
        }
    }

    private var tabType = TAB_WHISPERS
    private var targetUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabType = arguments?.getInt(ARG_TAB) ?: TAB_WHISPERS
        targetUserId = arguments?.getString(ARG_USER_ID) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_info_tab, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.tabRecyclerView)
        val emptyText = view.findViewById<TextView>(R.id.tabEmptyText)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
        val loginUserId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""

        when (tabType) {
            TAB_WHISPERS -> loadWhispers(recyclerView, emptyText, loginUserId)
            TAB_FOLLOWING -> loadFollowList(recyclerView, emptyText, Constants.ENDPOINT_USER_FOLLOWING)
            TAB_FOLLOWERS -> loadFollowList(recyclerView, emptyText, Constants.ENDPOINT_USER_FOLLOWERS)
            TAB_LIKES -> loadWhisperList(recyclerView, emptyText, loginUserId, Constants.ENDPOINT_USER_LIKES, "いいねした投稿はまだありません")
        }
    }

    private fun loadWhispers(recyclerView: RecyclerView, emptyText: TextView, loginUserId: String) {
        loadWhisperList(recyclerView, emptyText, loginUserId, Constants.ENDPOINT_USER_WHISPERS, "ささやきはまだありません", arrayKeyHint = "whisper")
    }

    private fun loadWhisperList(
        recyclerView: RecyclerView,
        emptyText: TextView,
        loginUserId: String,
        baseEndpoint: String,
        emptyMessage: String,
        arrayKeyHint: String = "whisper"
    ) {
        ApiClient.get(requireContext(), "$baseEndpoint/$targetUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val arr = root.optJSONArray(arrayKeyHint)
                    ?: root.optJSONArray("whisper_line")
                    ?: JSONArray()
                val list = TimelineActivity.parseWhispers(arr)
                requireActivity().runOnUiThread {
                    emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    if (list.isEmpty()) emptyText.text = emptyMessage
                    recyclerView.adapter = buildWhisperAdapter(list, loginUserId, recyclerView, emptyText, emptyMessage, baseEndpoint)
                }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    private fun buildWhisperAdapter(
        list: List<WhisperRowData>,
        loginUserId: String,
        recyclerView: RecyclerView,
        emptyText: TextView,
        emptyMessage: String,
        baseEndpoint: String
    ) = WhisperAdapter(
        whisperList = list,
        loginUserId = loginUserId,
        onLikeClick = { item ->
            val json = JSONObject().apply { put("whisper_id", item.whisperId.toIntOrNull() ?: 0) }
            ApiClient.post(requireContext(), Constants.ENDPOINT_LIKE, json, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) return
                    requireActivity().runOnUiThread {
                        loadWhisperList(recyclerView, emptyText, loginUserId, baseEndpoint, emptyMessage)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {}
            })
        },
        onRetweetClick = { item ->
            ApiClient.post(requireContext(), "${Constants.ENDPOINT_RETWEET}/${item.whisperId}/retweet", JSONObject(), object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) return
                    requireActivity().runOnUiThread {
                        loadWhisperList(recyclerView, emptyText, loginUserId, baseEndpoint, emptyMessage)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {}
            })
        },
        onUserClick = { item ->
            startActivity(Intent(requireContext(), UserInfoActivity::class.java).apply {
                putExtra("userId", item.userId)
            })
        },
        onWhisperClick = { item -> startActivity(item.toDetailIntent(requireContext())) }
    )

    private fun loadFollowList(recyclerView: RecyclerView, emptyText: TextView, baseEndpoint: String) {
        ApiClient.get(requireContext(), "$baseEndpoint/$targetUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val arr = root.optJSONArray("user_line") ?: JSONArray()
                val list = mutableListOf<UserRowData>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(UserRowData(
                        userName = obj.optString("name", ""),
                        followCount = obj.optInt("follows_count", 0),
                        followerCount = obj.optInt("followers_count", 0),
                        userImageResId = R.mipmap.ic_launcher
                    ))
                }
                requireActivity().runOnUiThread {
                    emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    if (list.isEmpty()) emptyText.text = "まだいません"
                    recyclerView.adapter = UserAdapter(list)
                }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}
