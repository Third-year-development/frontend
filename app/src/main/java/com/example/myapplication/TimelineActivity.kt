package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import kotlin.math.abs

class TimelineActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager2

    // 右スワイプでドロワーを開くための追跡変数
    private var swipeStartX = 0f
    private var swipeStartY = 0f
    private var drawerTriggered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline)
        setupToolbar()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        if (prefs.getString(Constants.PREF_USER_ID, null) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        val postButton = findViewById<FloatingActionButton>(R.id.postButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager.adapter = TimelinePagerAdapter(this)

        val tabTitles = listOf(getString(R.string.tab_recommended), getString(R.string.tab_following))
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        postButton.setOnClickListener {
            startActivity(Intent(this, WhisperPostActivity::class.java))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val drawer = drawerLayout
        if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    swipeStartX = ev.x
                    swipeStartY = ev.y
                    drawerTriggered = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!drawerTriggered && viewPager.currentItem == 0) {
                        val dx = ev.x - swipeStartX
                        val dy = abs(ev.y - swipeStartY)
                        // 右方向に60px以上かつ横成分が縦の2倍以上
                        if (dx > 60f && dx > dy * 2f) {
                            drawerTriggered = true
                            drawer.openDrawer(GravityCompat.START)
                            // 以降のイベントをキャンセルしてViewPager2に渡さない
                            val cancel = MotionEvent.obtain(ev).also {
                                it.action = MotionEvent.ACTION_CANCEL
                            }
                            super.dispatchTouchEvent(cancel)
                            cancel.recycle()
                            return true
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    drawerTriggered = false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        fun parseWhispers(jsonArray: JSONArray): List<WhisperRowData> {
            val list = mutableListOf<WhisperRowData>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val userObj = obj.optJSONObject("user")
                val retweeterName = userObj?.optString("name") ?: ""

                val parentObj = obj.optJSONObject("parent")
                val isRetweet = parentObj != null

                // リツイートの場合、元ささやきの情報を表示する
                val displayUserObj = if (isRetweet) parentObj!!.optJSONObject("user") else userObj
                val displayUserId = (displayUserObj?.optInt("id") ?: 0).toString()
                val displayUserName = displayUserObj?.optString("name") ?: ""
                val displayContent = if (isRetweet) parentObj!!.optString("content", "") else obj.optString("content", "")
                val displayWhisperId = if (isRetweet) parentObj!!.optInt("id").toString() else obj.getInt("id").toString()

                val sourceObj = if (isRetweet) parentObj!! else obj
                val likeCount = sourceObj.optInt("liked_by_count", sourceObj.optInt("likedBy_count", 0))
                val isLiked = sourceObj.optBoolean("liked_by_me", false)
                val retweetCount = sourceObj.optInt("retweets_count", 0)
                val isRetweeted = sourceObj.optBoolean("retweeted_by_me", false)

                list.add(WhisperRowData(
                    whisperId = displayWhisperId,
                    userId = displayUserId,
                    userName = displayUserName,
                    content = displayContent,
                    goodCount = likeCount,
                    isLiked = isLiked,
                    retweetCount = retweetCount,
                    isRetweeted = isRetweeted,
                    retweetedByName = if (isRetweet) retweeterName else null
                ))
            }
            return list
        }
    }
}
