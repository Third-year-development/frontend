package com.example.myapplication

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userIdEdit = findViewById<EditText>(R.id.userIdEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createButton = findViewById<Button>(R.id.createButton)

        loginButton.setOnClickListener {
            val userId = userIdEdit.text.toString().trim()
            val password = passwordEdit.text.toString()

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "\u30e6\u30fc\u30b6\u30fcID\u3068\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, TimelineActivity::class.java))
        }

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }
    }
}
