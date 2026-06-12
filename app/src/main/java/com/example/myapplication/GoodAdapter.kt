package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 3. アダプター作成（クラス名：GoodAdapter [引数：MutableList<GoodRowData>、Context]）
// 3-1. RecyclerView.Adapterクラスを継承する
class GoodAdapter(
    private val dataList: MutableList<GoodRowData>,
    private val context: Context
) : RecyclerView.Adapter<GoodAdapter.ViewHolder>() {

    // 3-2. ビューホルダー（内部クラス）
    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        // 3-2-1. 画面デザインで定義したオブジェクトを変数として宣言する
        val userImage: ImageView    // 1. アイコン画像
        val userNameText: TextView  // 2. UserName
        val whisperText: TextView   // 3. Whisper
        val goodCntText: TextView   // 5. gcnt (いいね数を入れるTextView)

        init {
            // 項目説明のID定義に完全に一致させて紐付け
            userImage = item.findViewById(R.id.userImage)
            userNameText = item.findViewById(R.id.userNameText)
            whisperText = item.findViewById(R.id.whisperText)
            goodCntText = item.findViewById(R.id.goodCntText) // 【修正】goodTextからgoodCntTextに変更
        }
    }

    // 3-3. ビューホルダー生成時（onCreateViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 3-3-1. いいね行情報の画面デザイン（good_recycle_row）をViewHolderに設定し、戻り値にセットする
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.good_recycle_row, parent, false)
        return ViewHolder(view)
    }

    // 3-4. ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]

        // 3-4-1. ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、ささやき内容、いいね数）をセットする
        holder.userNameText.text = currentItem.userName
        holder.whisperText.text = currentItem.whisperText
        holder.goodCntText.text = currentItem.Good.toString()

        // 3-4-2. userImageのクリックイベントリスナーを生成する
        holder.userImage.setOnClickListener {
            // 3-4-2-1. Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する
            // 【修正】第2引数を GoodAdapter から 実際のユーザ情報画面のActivity名（例: UserInfoActivity）に変更してください
            val intent = Intent(context, UserInfoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK

                // 3-4-2-2. インテントに対象行のユーザIDをセットする
                putExtra("USER_ID", currentItem.userId)
            }

            // 3-4-2-3. ユーザ情報画面に遷移する
            context.startActivity(intent)
        }
    }

    // 3-5. 行数取得時（getItemCount処理）
    override fun getItemCount(): Int {
        // 3-5-1. 行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataList.size
    }
}