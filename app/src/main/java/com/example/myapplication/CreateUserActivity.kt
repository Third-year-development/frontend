package com.example.myapplication

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

        cancelButton.setOnClickListener {
            finish()
        }

        rePasswordEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptCreateUser(userNameEdit, emailEdit, passwordEdit, rePasswordEdit)
                true
            } else {
                false
            }
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

        if (userName.isEmpty()) {
            userNameEdit.error = getString(R.string.create_user_name_required)
            hasError = true
        } else {
            userNameEdit.error = null
        }

        if (email.isEmpty()) {
            emailEdit.error = getString(R.string.create_user_email_required)
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.error = getString(R.string.create_user_email_invalid)
            hasError = true
        } else {
            emailEdit.error = null
        }

        if (password.isEmpty()) {
            passwordEdit.error = getString(R.string.create_user_password_required)
            hasError = true
        } else if (password.length < 6) {
            passwordEdit.error = getString(R.string.create_user_password_short)
            hasError = true
        } else {
            passwordEdit.error = null
        }

        if (rePassword.isEmpty()) {
            rePasswordEdit.error = getString(R.string.create_user_repassword_required)
            hasError = true
        } else if (password != rePassword) {
            rePasswordEdit.error = getString(R.string.create_user_password_mismatch)
            hasError = true
        } else {
            rePasswordEdit.error = null
        }

        if (hasError) {
            return
        }

        Toast.makeText(
            this,
            getString(R.string.create_user_not_implemented_message),
            Toast.LENGTH_SHORT
        ).show()
    }
}
