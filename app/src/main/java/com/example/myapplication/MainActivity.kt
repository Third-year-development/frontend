package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showLoginScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_timeline -> {
                showToast(getString(R.string.menu_timeline))
                true
            }
            R.id.action_search -> {
                showToast(getString(R.string.menu_search))
                true
            }
            R.id.action_whisper -> {
                showToast(getString(R.string.menu_whisper))
                true
            }
            R.id.action_profile -> {
                showToast(getString(R.string.menu_profile))
                true
            }
            R.id.action_user_edit -> {
                showProfileEditScreen()
                true
            }
            R.id.action_logout -> {
                showLoginScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoginScreen() {
        setContentView(R.layout.activity_login)
        setupToolbar(getString(R.string.login_title))
        applySystemBarPadding(R.id.login_root)

        findViewById<View>(R.id.button_login).setOnClickListener {
            showToast(getString(R.string.login_not_connected))
        }
        findViewById<View>(R.id.button_create_user).setOnClickListener {
            showUserCreateScreen()
        }
    }

    private fun showUserCreateScreen() {
        setContentView(R.layout.activity_user_create)
        setupToolbar(getString(R.string.user_create_title))
        applySystemBarPadding(R.id.user_create_root)

        findViewById<View>(R.id.button_save_user).setOnClickListener {
            showToast(getString(R.string.user_create_not_connected))
        }
        findViewById<View>(R.id.button_cancel_user_create).setOnClickListener {
            showLoginScreen()
        }
    }

    private fun showProfileEditScreen() {
        setContentView(R.layout.activity_profile_edit)
        setupToolbar(getString(R.string.profile_edit_title))
        applySystemBarPadding(R.id.profile_edit_root)

        findViewById<View>(R.id.button_change_profile).setOnClickListener {
            showToast(getString(R.string.profile_edit_not_connected))
        }
        findViewById<View>(R.id.button_cancel_profile_edit).setOnClickListener {
            showLoginScreen()
        }
    }

    private fun setupToolbar(title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    private fun applySystemBarPadding(rootViewId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootViewId)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
