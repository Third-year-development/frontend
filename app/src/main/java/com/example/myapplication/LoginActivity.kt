package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var toCreateUserText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailEdit = findViewById(R.id.emailEdit)
        passwordEdit = findViewById(R.id.passwordEdit)
        loginButton = findViewById(R.id.loginButton)
        toCreateUserText = findViewById(R.id.toCreateUserText)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 有効なセッションが残っていればログイン画面をスキップ
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val token = prefs.getString(Constants.PREF_TOKEN, null)
        val expiry = prefs.getLong(Constants.PREF_LOGIN_EXPIRY, 0L)
        if (token != null && System.currentTimeMillis() < expiry) {
            startActivity(Intent(this, TimelineActivity::class.java))
            finish()
            return
        }

        loginButton.setOnClickListener { attemptLogin() }
        toCreateUserText.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        var hasError = false

        if (email.isEmpty()) { emailEdit.error = getString(R.string.login_error_empty_email); hasError = true }
        else emailEdit.error = null

        if (password.isEmpty()) { passwordEdit.error = getString(R.string.login_error_empty_password); hasError = true }
        else passwordEdit.error = null

        if (hasError) return

        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        // ログインはトークン不要なので postNoAuth を使用
        ApiClient.postNoAuth(Constants.ENDPOINT_LOGIN, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_error_failed), Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val body = response.body?.string() ?: return
                val result = JSONObject(body)
                val token = result.getString("token")
                val user = result.getJSONObject("user")
                val userId = user.getInt("id").toString()
                val userName = user.getString("name")

                val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                prefs.edit().apply {
                    putString(Constants.PREF_TOKEN, token)
                    putString(Constants.PREF_USER_ID, userId)
                    putString(Constants.PREF_USER_NAME, userName)
                    putLong(Constants.PREF_LOGIN_EXPIRY, System.currentTimeMillis() + Constants.LOGIN_EXPIRY_MS)
                    apply()
                }

                runOnUiThread {
                    startActivity(Intent(this@LoginActivity, TimelineActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_error_network), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
