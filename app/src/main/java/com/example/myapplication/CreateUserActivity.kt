package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
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

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val createButton = findViewById<Button>(R.id.createButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val userNameEdit = findViewById<EditText>(R.id.userNameEdit)
        val emailEdit = findViewById<EditText>(R.id.userIdEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)
        val rePasswordEdit = findViewById<EditText>(R.id.rePasswordEdit)

        createButton.setOnClickListener {
            attemptCreateUser(userNameEdit, emailEdit, passwordEdit, rePasswordEdit)
        }
        cancelButton.setOnClickListener { finish() }

        rePasswordEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptCreateUser(userNameEdit, emailEdit, passwordEdit, rePasswordEdit)
                true
            } else false
        }
    }

    private fun attemptCreateUser(
        userNameEdit: EditText,
        emailEdit: EditText,
        passwordEdit: EditText,
        rePasswordEdit: EditText
    ) {
        val userName = userNameEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        val rePassword = rePasswordEdit.text.toString()
        var hasError = false

        if (userName.isEmpty()) { userNameEdit.error = getString(R.string.create_user_name_required); hasError = true }
        else userNameEdit.error = null

        if (email.isEmpty()) { emailEdit.error = getString(R.string.create_user_email_required); hasError = true }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailEdit.error = getString(R.string.create_user_email_invalid); hasError = true }
        else emailEdit.error = null

        if (password.isEmpty()) { passwordEdit.error = getString(R.string.create_user_password_required); hasError = true }
        else if (password.length < 6) { passwordEdit.error = getString(R.string.create_user_password_short); hasError = true }
        else passwordEdit.error = null

        if (rePassword.isEmpty()) { rePasswordEdit.error = getString(R.string.create_user_repassword_required); hasError = true }
        else if (password != rePassword) { rePasswordEdit.error = getString(R.string.create_user_password_mismatch); hasError = true }
        else rePasswordEdit.error = null

        if (hasError) return

        val json = JSONObject().apply {
            put("name", userName)
            put("email", email)
            put("password", password)
        }

        ApiClient.postNoAuth(Constants.ENDPOINT_CREATE_USER, json, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CreateUserActivity, "ユーザ作成に失敗しました", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                // 登録成功後はトークンを保存してタイムラインへ
                val body = response.body?.string() ?: return
                val result = JSONObject(body)
                val token = result.getString("token")
                val user = result.getJSONObject("user")
                val userId = user.getInt("id").toString()
                val userName2 = user.getString("name")

                val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                prefs.edit().apply {
                    putString(Constants.PREF_TOKEN, token)
                    putString(Constants.PREF_USER_ID, userId)
                    putString(Constants.PREF_USER_NAME, userName2)
                    putLong(Constants.PREF_LOGIN_EXPIRY, System.currentTimeMillis() + Constants.LOGIN_EXPIRY_MS)
                    apply()
                }

                runOnUiThread {
                    Toast.makeText(this@CreateUserActivity, "ユーザを作成しました", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateUserActivity, TimelineActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CreateUserActivity, "通信エラーが発生しました", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
