package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView


class  WhisperAdapter(
    private  val whisperList: MutableList<WhisperRowData>,
    private  val context: Context
): RecyclerView.Adapter<WhisperAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage: ImageView =
            itemView.findViewById(R.id.userImage)
        val userNameText: TextView =
            itemView.findViewById(R.id.userNameText)
        val whisperText: TextView =
            itemView.findViewById(R.id.WhisperText)
        val goodImage: ImageView =
            itemView.findViewById(R.id.goodImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.whsper_recycle_row,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = whisperList[position]

        holder.userNameText.text = item.userName
        holder.whisperText.text = item.whisperText


        holder.goodImage.setImageResource(
            if (item.isLiked)
            android.R.drawable.btn_star_big_on

            else
                android.R.drawable.btn_star_big_off
        )
        holder.userImage.setOnClickListener {
//            val intent = Intent(context,UserInfoActivity::class.java)
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("userId",item.userId)
//            context.startActivity(intent)
        }
        holder.goodImage.setOnClickListener {

            try {
                item.isLiked = !item.isLiked
                notifyItemChanged(position)
            }catch(e: Exception){
                Toast.makeText(context, "エラーが発生しました", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return whisperList.size
    }
}
