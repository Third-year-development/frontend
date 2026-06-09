package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


 open class OverFlowMenuActivity : AppCompatActivity() {
    private lateinit var searchText: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var userRadio: RadioButton
    private lateinit var whisperRadio: RadioButton
    private lateinit var searchEdit: EditText
    private lateinit var searchButton: Button
    private lateinit var searchRecycle: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        searchText = findViewById(R.id.searchText)
        radioGroup = findViewById(R.id.radioGroup)
        userRadio = findViewById(R.id.userRadio)
        whisperRadio = findViewById(R.id.whisperRadio)
        searchEdit = findViewById(R.id.searchEdit)
        searchButton = findViewById(R.id.searchButton)
        searchRecycle = findViewById(R.id.searchRecycle)

        searchRecycle.layoutManager = LinearLayoutManager(this)
        searchRecycle.visibility = View.GONE

        searchButton.setOnClickListener {
            executeSearch()
        }

        val mainView = findViewById<View>(R.id.main)
        if(mainView != null){
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        }

    }
    private fun executeSearch(){
        val query = searchEdit.text.toString().trim()

        if(query.isEmpty()){
            Toast.makeText(this, "検索内容を入力してください", Toast.LENGTH_SHORT).show()
            return
        }
        val checkedRadioId = radioGroup.checkedRadioButtonId
//        if (checkedRadioId == -1) {
//            Toast.makeText(
//                this, "検索タイプを選択してください",
//                Toast.LENGTH_SHORT).show()
//
//            return
//        }

        ApiClient.fetchSearchResults(query, checkedRadioId, object : ApiCallback<SearchResponse>{
            override fun  onResponse(status:  Int,response: SearchResponse?) {

                if (status !in 200..299) {

                    val errorMsg = response?.errorMessage ?: "通信エラーが発生しました"
                    Toast.makeText(this@OverFlowMenuActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    return
                }

                if (checkedRadioId == R.id.userRadio) {
                val userList = response?.userResults?.mapNoNull { userInfo ->
                        UserRowData(
                            userName = userInfo.userName,
                            followCount = userInfo.followCount,
                            followerCount = userInfo.followerCount,
                            userImageResId = R.mipmap.ic_launcher
                        )
                } ?: emptyList()


                val userAdapter = UserAdapter(userList)
                searchRecycle.adapter = userAdapter

                searchRecycle.visibility = View.VISIBLE

                }else if (checkedRadioId == R.id.whisperRadio){

                    val whisperList = response?.whisperResults?.toMutableList() ?: mutableListOf()




                    val whisperAdapter = WhisperAdapter(whisperList)

                    searchRecycle.adapter = whisperAdapter

                    searchRecycle.visibility = View.VISIBLE
                }
            }
            override fun onFailure(throwable: Throwable){

                val errorMsg = throwable.message ?: "通信に失敗しました"
                Toast.makeText(this@OverFlowMenuActivity,errorMsg,Toast.LENGTH_SHORT).show()
            }
        })
    }
}
