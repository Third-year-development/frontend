package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.jvm.java

// １．OverFlowMenuActivityクラスを継承する
class WhisperActivity : OverFlowMenuActivity() {
    // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
    private lateinit var whisperText: TextView
    private lateinit var whisperEdit: EditText
    private lateinit var whisperButton: Button
    private lateinit var cancelButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_whisper)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        whisperText= findViewById(R.id.whisperText)
        whisperEdit = findViewById(R.id.whisperEdit)
        whisperButton = findViewById(R.id.whisperButton)
        cancelButton = findViewById(R.id.cancelButton)

        // このように直接取得できます
        val userId = loginUserId
        //2-2．グローバル変数のログインユーザーIDを取得。
        // 例：TextViewに表示する場合
        //2-3．whisperButtonのクリックイベントリスナーを作成する
        whisperButton.setOnClickListener {
            // 2-3-1. 入力チェック
            val wisper = whisperEdit.text.toString()


            if (wisper.isEmpty()) {
                Toast.makeText(this, "ささやく内容を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            // 2-3-2. ログイン認証APIをリクエストする（※非同期処理のイメージ）
            // ここでは架空の関数「WhisperApi.request」として表現します

            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようデータ形式を設定
            val mediaType : MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ(APIに渡したいパラメータを設定)
            val requestBodyJson = JSONObject().apply {
                put("wisper", wisper) // 取得済みのwisperをセット

            }
            // BodyのデータをAPIに送るためにRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
            val request = Request.Builder()
                .url("http://10.0.2.2/SampleProject/sample.php") // URL設定
                .post(requestBody) // リクエストするパラメータ設定
                .build()


            client.newCall(request!!).enqueue(object : Callback {

                // 2-3-2-1. 正常にレスポンスを受け取った時（コールバック処理）
                override fun onResponse(call: Call, response: Response) {
                    val responseCode = response.code // HTTPステータスコードを取得
                    val body = response.body?.string() // サーバーからのメッセージ（JSONなど）

                    // 画面操作（UIスレッド）に戻って処理を行う
                    runOnUiThread {
                        // 2-3-2-1-1. ステータスコードが200番台以外（通信エラー）の場合
                        if (responseCode !in 200..299) {
                            // 2-3-2-1-1-1. 受け取ったメッセージをトースト表示
                            Toast.makeText(this@WhisperActivity, "エラー: $body", Toast.LENGTH_SHORT).show()
                            // ここで処理が終了する
                        } else {
                            // 2-3-2-1-2. タイムライン画面に遷移する
                            val intent = Intent(this@WhisperActivity, TimelineActivity::class.java)
                            startActivity(intent)
                            // 2-3-2-1-3. 自分の画面を閉じる
                            finish()
                        }
                    }
                }

                // 2-3-2-2. リクエストが失敗した時（コールバック処理）
                override fun onFailure(call: Call, e: IOException) {
                    // 2-3-2-2-1. エラーメッセージをトースト表示
                    runOnUiThread {
                        Toast.makeText(this@WhisperActivity, "通信に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        cancelButton.setOnClickListener{
            //2-4-1．自分の画面を閉じる
            finish()
        }
    }
}

