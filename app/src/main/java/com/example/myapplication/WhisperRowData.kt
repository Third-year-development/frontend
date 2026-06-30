package com.example.myapplication

data class WhisperRowData(
    val whisperId: String,
    val userId: String,
    val userName: String,
    val content: String,
    val goodCount: Int = 0,
    var isLiked: Boolean = false,
    val retweetCount: Int = 0,
    val isRetweeted: Boolean = false,
    val retweetedByName: String? = null,
    val imagePath: String = ""
) {
    val whisperNo: Int
        get() = whisperId.toIntOrNull() ?: 0

    val whisperText: String
        get() = content
}
