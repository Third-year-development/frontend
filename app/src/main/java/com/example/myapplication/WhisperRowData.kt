package com.example.myapplication

import android.content.Context
import android.content.Intent

data class WhisperRowData(
    val whisperId: String,
    val userId: String,
    val userName: String,
    val content: String,
    val goodCount: Int,
    val isLiked: Boolean,
    val retweetCount: Int = 0,
    val isRetweeted: Boolean = false,
    val retweetedByName: String? = null  // non-null = このアイテムはこのユーザがリツイートしたもの
)

fun WhisperRowData.toDetailIntent(context: Context): Intent =
    Intent(context, WhisperDetailActivity::class.java).apply {
        putExtra("whisperId", whisperId)
        putExtra("userId", userId)
        putExtra("userName", userName)
        putExtra("content", content)
        putExtra("goodCount", goodCount)
        putExtra("isLiked", isLiked)
    }
