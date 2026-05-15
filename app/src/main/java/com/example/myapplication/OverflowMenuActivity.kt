package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OverflowMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       when (item.itemId){
//
//           R.id.timeline -> {
//                 val intent = Intent(this, TimelineActivity::class.java)
//                startActivity(intent)
//
//                return true
//            }
//
//                R.id.search -> {
//                     val intent = Intent(this, SearchActivity::class.java)
//                    startActivity(intent)
//                    return true
//                }
//        R.id.whisper -> {
//            val intent = Intent(this, WhisperActivity::class.java)
//            startActivity(intent)
//            return true
//        }
//        R.id.myprofile -> {
//            val intent = Intent(this,MyprofileActivity::class.java)
//           intent.putExtra("loginUserId", loginUserId)
//              startActivity(intent)
//            return true
//        }
//        R.id.profileedit ->{
//            val intent = Intent(this, ProfileeditActivity::class.java)
//           intent.putExtra("loginUserId", loginUserId)
//            startActivity(intent)
//            return true
//        }

//           R.id.logout ->{
//               loginUserId = ""
//               val intent = Intent(this, LogoutActivity::class.java)
//               intent.fiags =
//                   Intent.FLAG_ACTIVITY_NEW_TASK or
//                   Intent.FLAG_ACTIVITY_CLEAR_TASK
//               startActivity(intent)
//
//
//               finish()
//               return true
//           }

    }
        return super.onOptionsItemSelected(item)
    }

}