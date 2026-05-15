package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.timeline -> {
                showPlaceholder(R.string.menu_timeline_title)
                true
            }

            R.id.search -> {
                showPlaceholder(R.string.menu_search_title)
                true
            }

            R.id.whisper -> {
                showPlaceholder(R.string.menu_whisper_title)
                true
            }

            R.id.myprofile -> {
                showPlaceholder(R.string.menu_profile_title)
                true
            }

            R.id.profileedit -> {
                showPlaceholder(R.string.menu_profile_edit_title)
                true
            }

            R.id.logout -> {
                Toast.makeText(this, getString(R.string.menu_logout_message), Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPlaceholder(titleResId: Int) {
        val message = getString(R.string.menu_navigation_placeholder, getString(titleResId))
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
