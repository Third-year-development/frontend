package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class WhisperDetailActivity : BaseActivity() {

    private lateinit var detailUserImage: ImageView
    private lateinit var detailUserNameText: TextView
    private lateinit var detailContentText: TextView
    private lateinit var detailLikeButton: ImageButton
    private lateinit var detailLikeCntText: TextView

    private lateinit var loginUserId: String
    private lateinit var whisper: WhisperRowData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_whisper_detail)
        setupToolbar()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        loginUserId = prefs.getString(Constants.PREF_USER_ID, "") ?: ""

        val whisperId   = intent.getStringExtra("whisperId") ?: run { finish(); return }
        val userId      = intent.getStringExtra("userId") ?: ""
        val userName    = intent.getStringExtra("userName") ?: ""
        val content     = intent.getStringExtra("content") ?: ""
        val goodCount   = intent.getIntExtra("goodCount", 0)
        val isLiked     = intent.getBooleanExtra("isLiked", false)
        whisper = WhisperRowData(whisperId, userId, userName, content, goodCount, isLiked)

        detailUserImage    = findViewById(R.id.detailUserImage)
        detailUserNameText = findViewById(R.id.detailUserNameText)
        detailContentText  = findViewById(R.id.detailContentText)
        detailLikeButton   = findViewById(R.id.detailLikeButton)
        detailLikeCntText  = findViewById(R.id.detailLikeCntText)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindWhisper(whisper)

        // ユーザ画像・名前タップ → プロフィール
        val goToProfile = {
            startActivity(Intent(this, UserInfoActivity::class.java).apply {
                putExtra("userId", whisper.userId)
            })
        }
        detailUserImage.setOnClickListener { goToProfile() }
        detailUserNameText.setOnClickListener { goToProfile() }

        detailLikeButton.setOnClickListener { toggleLike() }
    }

    private fun bindWhisper(w: WhisperRowData) {
        detailUserNameText.text = w.userName
        detailContentText.text = w.content
        detailLikeCntText.text = w.goodCount.toString()
        detailLikeButton.setImageResource(
            if (w.isLiked) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    private fun toggleLike() {
        val json = JSONObject().apply {
            put("whisper_id", whisper.whisperId.toIntOrNull() ?: 0)
            put("liked", !whisper.isLiked)
        }
        ApiClient.post(this, Constants.ENDPOINT_LIKE, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val newCount = if (whisper.isLiked) whisper.goodCount - 1 else whisper.goodCount + 1
                whisper = whisper.copy(isLiked = !whisper.isLiked, goodCount = newCount)
                runOnUiThread { bindWhisper(whisper) }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WhisperDetailActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
