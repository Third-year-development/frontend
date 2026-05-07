package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CreateusersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        /// 1-1 画面デザインで定義したオブジェクトを変数として宣言する。


        val createbutton: Button = findViewById(R.id.createButton)
        val canselbutton: Button= findViewById(R.id.cancelButton)
        val creteUserText: TextView = findViewById(R.id.CreateUserText)
        val userNameEdit: EditText = findViewById (R.id.userNameEdit)
        val useridEdit: EditText = findViewById (R.id.userIdEdit)
        val passwordEdit: EditText = findViewById (R.id.passwordEdit)
        val repasswordEdit: EditText = findViewById (R.id.rePasswordEdit)

        setContentView(R.layout.activity_createusers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // １－２．createButtonのクリックイベントリスナーを作成する
        createbutton.setOnClickListener {
            // １－２－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            if(userNameEdit.text.toString().isEmpty() ||
                useridEdit.text.toString().isEmpty() ||
                passwordEdit.text.toString().isEmpty() ||
                repasswordEdit.text.toString().isEmpty()){
                Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_SHORT).show()
            }

            // １－２－２．パスワードと確認パスワードの内容が違う時、エラーメッセージをトースト表示して処理を終了させる
            if(){
                Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_SHORT).show()
            }

            // １－２－３．ユーザ作成処理APIをリクエストしてユーザの追加を行う
            // HTTP接続用インスタンス生成（こいつを使ってPHPと通信する）
            val client = OkHttpClient()

            // ↓↓↓↓↓↓↓↓↓　通信に必要な設定を行う　↓↓↓↓↓↓↓↓↓
            // JSON形式でパラメータを送るようデータ形式を設定
            val mediaType : MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ(APIに渡したいパラメータを設定)
            val requestBodyJson = JSONObject().apply {
                put("email", xxxxxxxxxxxxxxxxxxxx)
                put("password", xxxxxxxxxxxxxxxxxxxx)
            }
            // BodyのデータをAPIに送るためにRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
            val request = Request.Builder()
                .url("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") // URL設定
                .post(requestBody) // リクエストするパラメータ設定
                .build()
            // ↑↑↑↑↑↑↑↑　通信に必要な設定を行う　↑↑↑↑↑↑↑↑

            // ↓↓↓↓↓↓↓↓↓　実際に通信してる処理　↓↓↓↓↓↓↓↓↓
            // リクエスト送信（非同期処理）
            client.newCall(request!!).enqueue(object : Callback {
                // リクエストが成功した場合の処理を実装
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    println("レスポンスを受信しました: $body")
                    // postメソッドを使うことでUIを操作することができる。(runOnUiThreadメソッドでも可)
                    textView.post { textView.text = body }
                }
                // リクエストが失敗した場合の処理を実装
                override fun onFailure(call: Call, e: IOException) {
                    // runOnUiThreadメソッドを使うことでUIを操作することができる。(postメソッドでも可)
                    runOnUiThread {
                        textView.text = "リクエストが失敗しました: ${e.message}"
                    }
                }
            })
            // ↑↑↑↑↑↑↑↑　実際に通信してる処理　↑↑↑↑↑↑↑↑
        }

        passwordEdit.setOnClickListener {
            Toast.makeText(this,"パスワードが一致しません", Toast.LENGTH_SHORT).show()
        }
    }
}