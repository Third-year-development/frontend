package com.example.myapplication

import org.json.JSONArray

object WhisperParser {
    fun parseWhispers(jsonArray: JSONArray): List<WhisperRowData> {
        val list = mutableListOf<WhisperRowData>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val userObj = obj.optJSONObject("user")
            val retweeterName = userObj?.optString("name") ?: ""

            val parentObj = obj.optJSONObject("parent")
            val isRetweet = parentObj != null

            val displayUserObj = if (isRetweet) parentObj!!.optJSONObject("user") else userObj
            val displayUserId = (displayUserObj?.optInt("id") ?: 0).toString()
            val displayUserName = displayUserObj?.optString("name") ?: ""
            val displayContent = if (isRetweet) parentObj!!.optString("content", "") else obj.optString("content", "")
            val displayWhisperId = if (isRetweet) parentObj!!.optInt("id").toString() else obj.getInt("id").toString()

            val sourceObj = if (isRetweet) parentObj!! else obj
            val likeCount = sourceObj.optInt("liked_by_count", sourceObj.optInt("likedBy_count", 0))
            val isLiked = sourceObj.optBoolean("liked_by_me", false)
            val retweetCount = sourceObj.optInt("retweets_count", 0)
            val isRetweeted = sourceObj.optBoolean("retweeted_by_me", false)

            list.add(
                WhisperRowData(
                    whisperId = displayWhisperId,
                    userId = displayUserId,
                    userName = displayUserName,
                    content = displayContent,
                    goodCount = likeCount,
                    isLiked = isLiked,
                    retweetCount = retweetCount,
                    isRetweeted = isRetweeted,
                    retweetedByName = if (isRetweet) retweeterName else null
                )
            )
        }
        return list
    }
}
