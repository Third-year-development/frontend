package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class WhisperPostActivity : BaseActivity() {
    private lateinit var whisperEdit: EditText
    private lateinit var postButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_whisper_post)
        setupToolbar()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        if (prefs.getString(Constants.PREF_TOKEN, null) == null) { finish(); return }

        whisperEdit = findViewById(R.id.whisperEdit)
        postButton = findViewById(R.id.postButton)
        cancelButton = findViewById(R.id.cancelButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        postButton.setOnClickListener { attemptPost() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun attemptPost() {
        val content = whisperEdit.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, getString(R.string.whisper_post_error_empty), Toast.LENGTH_SHORT).show()
            return
        }

        // バックエンドのフィールド名は "text"
        val json = JSONObject().apply { put("text", content) }

        ApiClient.post(this, Constants.ENDPOINT_POST_WHISPER, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@WhisperPostActivity, getString(R.string.whisper_post_error), Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                runOnUiThread {
                    Toast.makeText(this@WhisperPostActivity, getString(R.string.whisper_post_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WhisperPostActivity, getString(R.string.whisper_post_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
