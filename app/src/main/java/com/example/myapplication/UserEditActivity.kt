package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class UserEditActivity : BaseActivity() {
    private lateinit var userEditText: TextView
    private lateinit var userImage: ImageView
    private lateinit var userIdText: TextView
    private lateinit var userNameEdit: EditText
    private lateinit var profileEdit: EditText
    private lateinit var changeButton: Button
    private lateinit var cancelButton: Button
    private lateinit var loginUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_edit)
        setupToolbar()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        loginUserId = prefs.getString(Constants.PREF_USER_ID, null) ?: run { finish(); return }

        userEditText = findViewById(R.id.userEditText)
        userImage = findViewById(R.id.userImage)
        userIdText = findViewById(R.id.userIdText)
        userNameEdit = findViewById(R.id.userNameEdit)
        profileEdit = findViewById(R.id.profileEdit)
        changeButton = findViewById(R.id.changeButton)
        cancelButton = findViewById(R.id.cancelButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getUserInfo()
        changeButton.setOnClickListener { updateUserInfo() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun getUserInfo() {
        // GET /api/v1/user → {"userprofile": {...}}
        ApiClient.get(this, Constants.ENDPOINT_GET_ME, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@UserEditActivity, "ユーザー情報取得エラー", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val user = root.optJSONObject("userprofile") ?: root
                val name = user.optString("name", "")
                val profileObj = user.optJSONObject("profile")
                val profile = profileObj?.optString("profile", "") ?: ""
                runOnUiThread {
                    userIdText.text = user.optString("email", "")
                    userNameEdit.setText(name)
                    profileEdit.setText(profile)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UserEditActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUserInfo() {
        val json = JSONObject().apply {
            put("name", userNameEdit.text.toString())
            put("profile", profileEdit.text.toString())
        }
        // POST /api/v1/users/profile/{id}
        ApiClient.post(this, "${Constants.ENDPOINT_UPDATE_USER}/$loginUserId", json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@UserEditActivity, getString(R.string.user_edit_error), Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                runOnUiThread {
                    Toast.makeText(this@UserEditActivity, getString(R.string.user_edit_success), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@UserEditActivity, UserInfoActivity::class.java).apply {
                        putExtra("userId", loginUserId)
                    })
                    finish()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UserEditActivity, "通信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
