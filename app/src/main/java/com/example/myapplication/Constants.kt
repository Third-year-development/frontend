package com.example.myapplication

object Constants {
    const val BASE_URL = "https://click.ecc.ac.jp/ecc/whisper26_a/api/v1"

    // 認証
    const val ENDPOINT_LOGIN = "$BASE_URL/auth/login"
    const val ENDPOINT_LOGOUT = "$BASE_URL/auth/logout"

    // ユーザ
    const val ENDPOINT_CREATE_USER = "$BASE_URL/users/register"
    const val ENDPOINT_GET_ME = "$BASE_URL/user"
    const val ENDPOINT_GET_USER = "$BASE_URL/users"            // GET /{id}
    const val ENDPOINT_UPDATE_USER = "$BASE_URL/users/profile" // POST /{id}

    // タイムライン・ささやき
    const val ENDPOINT_TIMELINE = "$BASE_URL/whispers"          // フォロー中
    const val ENDPOINT_TIMELINE_ALL = "$BASE_URL/whispers/all"  // おすすめ（全件）
    const val ENDPOINT_POST_WHISPER = "$BASE_URL/whispers"
    const val ENDPOINT_LIKE = "$BASE_URL/likecheck"

    // フォロー
    const val ENDPOINT_FOLLOW = "$BASE_URL/followcheck"
    const val ENDPOINT_FOLLOWING = "$BASE_URL/following"
    const val ENDPOINT_FOLLOWERS = "$BASE_URL/followers"
    const val ENDPOINT_USER_FOLLOWING = "$BASE_URL/user/following" // GET /{id}
    const val ENDPOINT_USER_FOLLOWERS = "$BASE_URL/user/followers" // GET /{id}

    // ユーザのささやき一覧・いいね一覧
    const val ENDPOINT_USER_WHISPERS = "$BASE_URL/user/whispers" // GET /{id}
    const val ENDPOINT_USER_LIKES = "$BASE_URL/user/likes"       // GET /{id}

    // 検索
    const val ENDPOINT_SEARCH_USERS = "$BASE_URL/search/users"       // GET /{keyword}
    const val ENDPOINT_SEARCH_WHISPERS = "$BASE_URL/search/whispers" // GET /{keyword}

    // SharedPreferences
    const val PREF_NAME = "wisper_prefs"
    const val PREF_USER_ID = "userId"
    const val PREF_USER_NAME = "userName"
    const val PREF_TOKEN = "token"
    const val PREF_LOGIN_EXPIRY = "loginExpiry"

    // 6ヶ月 (ミリ秒)
    const val LOGIN_EXPIRY_MS = 6L * 30 * 24 * 60 * 60 * 1000
}
