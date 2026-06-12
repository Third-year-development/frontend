package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class TimelineFragment : Fragment() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private var tabIndex = 0  // 0 = おすすめ, 1 = フォロー中

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabIndex = arguments?.getInt(ARG_TAB) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.timelineRecycle)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        swipeRefresh.setOnRefreshListener { loadTimeline() }
        loadTimeline()
    }

    override fun onResume() {
        super.onResume()
        loadTimeline()
    }

    fun loadTimeline() {
        val prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
        val loginUserId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""
        val endpoint = if (tabIndex == 0) Constants.ENDPOINT_TIMELINE_ALL else Constants.ENDPOINT_TIMELINE

        ApiClient.get(requireContext(), endpoint, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    activity?.runOnUiThread {
                        swipeRefresh.isRefreshing = false
                        Toast.makeText(requireContext(), getString(R.string.timeline_error_load), Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val jsonArray: JSONArray = if (root.has("whisper")) root.getJSONArray("whisper") else JSONArray()
                val whisperList = TimelineActivity.parseWhispers(jsonArray)
                activity?.runOnUiThread {
                    swipeRefresh.isRefreshing = false
                    recyclerView.adapter = WhisperAdapter(
                        whisperList, loginUserId,
                        onLikeClick = { item -> toggleLike(item, loginUserId) },
                        onRetweetClick = { item -> toggleRetweet(item) },
                        onUserClick = { item ->
                            startActivity(Intent(requireContext(), UserInfoActivity::class.java).apply {
                                putExtra("userId", item.userId)
                            })
                        },
                        onWhisperClick = { item ->
                            startActivity(item.toDetailIntent(requireContext()))
                        }
                    )
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), getString(R.string.timeline_error_load), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun toggleLike(item: WhisperRowData, loginUserId: String) {
        val json = JSONObject().apply {
            put("whisper_id", item.whisperId.toIntOrNull() ?: 0)
            put("liked", !item.isLiked)
        }
        ApiClient.post(requireContext(), Constants.ENDPOINT_LIKE, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) activity?.runOnUiThread { loadTimeline() }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    private fun toggleRetweet(item: WhisperRowData) {
        ApiClient.post(requireContext(), "${Constants.ENDPOINT_RETWEET}/${item.whisperId}/retweet", JSONObject(), object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) activity?.runOnUiThread { loadTimeline() }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    companion object {
        private const val ARG_TAB = "tab"

        fun newInstance(tabIndex: Int) = TimelineFragment().apply {
            arguments = Bundle().apply { putInt(ARG_TAB, tabIndex) }
        }
    }
}
