package com.example.myapplication

import android.media.Image
import org.w3c.dom.Text
import java.nio.file.Path


data class WhisperRowData(
    val userId: String,
    val userName: String,
    val whisperNo:Int,
    val whisperText: String,
    val imagePath: String,
    var isLiked: Boolean

)