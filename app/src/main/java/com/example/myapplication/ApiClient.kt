package com.example.myapplication

import android.content.Context
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object ApiClient {
    val client = OkHttpClient()
    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    private fun token(context: Context): String {
        val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        return "Bearer ${prefs.getString(Constants.PREF_TOKEN, "")}"
    }

    fun get(context: Context, url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token(context))
            .addHeader("Accept", "application/json")
            .get().build()
        client.newCall(request).enqueue(callback)
    }

    fun post(context: Context, url: String, body: JSONObject, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token(context))
            .addHeader("Accept", "application/json")
            .post(body.toString().toRequestBody(JSON_MEDIA)).build()
        client.newCall(request).enqueue(callback)
    }

    fun postNoAuth(url: String, body: JSONObject, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .post(body.toString().toRequestBody(JSON_MEDIA)).build()
        client.newCall(request).enqueue(callback)
    }

    // 後方互換（context なしシグネチャ — トークン不要な呼び出し用）
    fun get(url: String, callback: Callback) {
        client.newCall(
            Request.Builder().url(url).addHeader("Accept", "application/json").get().build()
        ).enqueue(callback)
    }

    fun post(url: String, body: JSONObject, callback: Callback) {
        client.newCall(
            Request.Builder().url(url).addHeader("Accept", "application/json")
                .post(body.toString().toRequestBody(JSON_MEDIA)).build()
        ).enqueue(callback)
    }

    fun put(url: String, body: JSONObject, callback: Callback) {
        client.newCall(
            Request.Builder().url(url).addHeader("Accept", "application/json")
                .put(body.toString().toRequestBody(JSON_MEDIA)).build()
        ).enqueue(callback)
    }
}
