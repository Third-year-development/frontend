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

        val emailEdit = findViewById<EditText>(R.id.userIdEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createButton = findViewById<Button>(R.id.createButton)

        loginButton.setOnClickListener {
            attemptLogin(emailEdit, passwordEdit)
        }

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }

        passwordEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin(emailEdit, passwordEdit)
                true
            } else {
                false
            }
        }
    }

    private fun attemptLogin(emailEdit: EditText, passwordEdit: EditText) {
        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString()
        var hasError = false

        if (email.isEmpty()) {
            emailEdit.error = getString(R.string.login_email_required)
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.error = getString(R.string.login_email_invalid)
            hasError = true
        } else {
            emailEdit.error = null
        }

        if (password.isEmpty()) {
            passwordEdit.error = getString(R.string.login_password_required)
            hasError = true
        } else {
            passwordEdit.error = null
        }

        if (hasError) {
            return
        }

        // API integration is not implemented on this branch yet, so block navigation here.
        Toast.makeText(
            this,
            getString(R.string.login_not_implemented_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToTimeline() {
        startActivity(Intent(this, TimelineActivity::class.java))
        finish()
    }
}
