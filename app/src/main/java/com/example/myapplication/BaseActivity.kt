package com.example.myapplication

import android.content.Intent
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject

abstract class BaseActivity : AppCompatActivity() {

    protected var drawerLayout: DrawerLayout? = null

    protected fun setupToolbar(toolbarId: Int = R.id.toolbar) {
        val toolbar = findViewById<Toolbar>(toolbarId)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        val drawer = drawerLayout ?: return

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.nav_open, R.string.nav_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navView)
        navView ?: return

        // ヘッダーを動的に追加
        val headerView = navView.inflateHeaderView(R.layout.nav_header)
        populateNavHeader(headerView, navView)

        navView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawers()
            handleNavItem(item)
            true
        }
    }

    private fun populateNavHeader(headerView: android.view.View, navView: NavigationView) {
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val loginUserId = prefs.getString(Constants.PREF_USER_ID, null) ?: return

        val nameText     = headerView.findViewById<TextView>(R.id.navHeaderName)
        val emailText    = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        val followCnt    = headerView.findViewById<TextView>(R.id.navHeaderFollowCnt)
        val followerCnt  = headerView.findViewById<TextView>(R.id.navHeaderFollowerCnt)

        // SharedPrefsから名前を即時表示
        nameText.text = prefs.getString(Constants.PREF_USER_NAME, "")

        // APIからフォロー数・メール取得
        ApiClient.get(this, "${Constants.ENDPOINT_GET_USER}/$loginUserId", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return
                val root = JSONObject(body)
                val user = root.optJSONObject("userprofile") ?: root
                runOnUiThread {
                    nameText.text  = user.optString("name", nameText.text.toString())
                    emailText.text = user.optString("email", "")
                    followCnt.text  = user.optInt("follows_count", 0).toString()
                    followerCnt.text = user.optInt("followers_count", 0).toString()
                }
            }
            override fun onFailure(call: Call, e: java.io.IOException) {}
        })

        // ヘッダータップ → 自分のプロフィール
        headerView.setOnClickListener {
            drawerLayout?.closeDrawers()
            startActivity(Intent(this, UserInfoActivity::class.java).apply {
                putExtra("userId", loginUserId)
            })
        }
    }

    override fun onBackPressed() {
        val drawer = drawerLayout
        if (drawer != null && drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawer.closeDrawers()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    private fun handleNavItem(item: MenuItem) {
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val loginUserId = prefs.getString(Constants.PREF_USER_ID, null)

        when (item.itemId) {
            R.id.menu_search   -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.menu_whisper  -> startActivity(Intent(this, WhisperPostActivity::class.java))
            R.id.menu_profile  -> {
                if (loginUserId != null) {
                    startActivity(Intent(this, UserInfoActivity::class.java).apply {
                        putExtra("userId", loginUserId)
                    })
                }
            }
            R.id.menu_user_edit -> {
                if (loginUserId != null) {
                    startActivity(Intent(this, UserEditActivity::class.java).apply {
                        putExtra("userId", loginUserId)
                    })
                }
            }
            R.id.menu_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("ログアウト")
                    .setMessage("ログアウトしますか？")
                    .setPositiveButton("ログアウト") { _, _ ->
                        ApiClient.post(this, Constants.ENDPOINT_LOGOUT, JSONObject(), object : Callback {
                            override fun onResponse(call: Call, response: Response) {}
                            override fun onFailure(call: Call, e: java.io.IOException) {}
                        })
                        prefs.edit().clear().apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
            }
        }
    }
}
