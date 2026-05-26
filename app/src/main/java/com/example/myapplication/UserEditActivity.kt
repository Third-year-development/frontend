package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.IOException
import okhttp3.Request
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class UserEditActivity : AppCompatActivity() {
    private  lateinit var  userEditText: TextView
    private  lateinit var  userImage: ImageView
    private  lateinit var  userIdText: TextView
    private  lateinit var  userNameEdit: EditText
    private  lateinit var  profileEdit: EditText
    private  lateinit var  changeButton: Button
    private  lateinit var  cancelButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_edit)

        //2-1.画面デザインで定義したオブジェクトを変換として宣言
        userEditText = findViewById(R.id.userEditText)
        userImage = findViewById(R.id.userImage)
        userIdText = findViewById(R.id.userIdText)
        userNameEdit = findViewById(R.id.userNameEdit)
        profileEdit  = findViewById(R.id.profileEdit)
        changeButton = findViewById(R.id.changeButton)
        cancelButton = findViewById(R.id.cancelButton)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val loginUserId = intent.getStringExtra("userId") ?: return


        getUserInfo(loginUserId)

        changeButton.setOnClickListener {
            updateUserInfo(loginUserId)
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
    private fun  getUserInfo(userId: String) {
        val client =OkHttpClient()

        val request = Request.Builder()
            .url("xxxxxxxxxxxxxxxxxxxxxxxxx=$userId")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback{
            override  fun onResponse(call: Call,response: Response){
                if (!response.isSuccessful){
                    runOnUiThread {
                        Toast.makeText(
                            this@UserEditActivity,
                            "ユーザー情報取得エラー",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
                val body =response.body?.string() ?: return

                val  json = JSONObject(body)
                val  userName = json.getString("userName")
                val  profile = json.getString("profile")


                runOnUiThread {
                    userIdText.text = userId
                    userNameEdit.setText(userName)
                    profileEdit.setText(profile)

                }
            }
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread {
                    Toast.makeText(
                        this@UserEditActivity,
                        "通信エラー",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    private fun updateUserInfo(userId: String){
        val client = OkHttpClient()

        val  json = JSONObject().apply {
            put("userId",userId)
            put("userName", userNameEdit.text.toString())
            put("profile",profileEdit.text.toString())
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val  requestBody =
            json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call,response: Response){
                if(!response.isSuccessful){
                    runOnUiThread {
                        Toast.makeText(
                            this@UserEditActivity,
                            "更新に失敗しました",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
//                runOnUiThread {
//                    startActivity(
//                        Intent(
//                            this@UserEditActivity,
//                            UserInfoActivity::class.java
//                        )
//                    )
//                    finish()
//                }
            }
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread {
                    Toast.makeText(
                        this@UserEditActivity,
                        "通信エラー",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}