package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class WhisperAdapter(
    private val whisperList: List<WhisperRowData>,
    private val loginUserId: String,
    private val onLikeClick: (WhisperRowData) -> Unit,
    private val onRetweetClick: ((WhisperRowData) -> Unit)? = null,
    private val onUserClick: ((WhisperRowData) -> Unit)? = null,
    private val onWhisperClick: ((WhisperRowData) -> Unit)? = null
) : RecyclerView.Adapter<WhisperAdapter.WhisperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhisperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.whisper_recycle_row, parent, false)
        return WhisperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WhisperViewHolder, position: Int) {
        val item = whisperList[position]

        // リツイートヘッダー
        if (item.retweetedByName != null) {
            holder.retweetHeader.visibility = View.VISIBLE
            holder.retweetHeader.text = "↺ ${item.retweetedByName} がリツイート"
        } else {
            holder.retweetHeader.visibility = View.GONE
        }

        holder.userNameText.text = item.userName
        holder.contentText.text = item.content
        holder.likeCntText.text = item.goodCount.toString()
        holder.retweetCntText.text = item.retweetCount.toString()

        holder.likeButton.setImageResource(
            if (item.isLiked) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
        holder.retweetButton.alpha = if (item.isRetweeted) 1.0f else 0.4f

        holder.likeButton.setOnClickListener { onLikeClick(item) }
        holder.retweetButton.setOnClickListener { onRetweetClick?.invoke(item) }

        val userClickListener = View.OnClickListener { onUserClick?.invoke(item) }
        holder.userImage.setOnClickListener(userClickListener)
        holder.userNameText.setOnClickListener(userClickListener)

        holder.card.setOnClickListener { onWhisperClick?.invoke(item) }
    }

    override fun getItemCount(): Int = whisperList.size

    class WhisperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.whisperCard)
        val retweetHeader: TextView = itemView.findViewById(R.id.retweetHeader)
        val userImage: ImageView = itemView.findViewById(R.id.whisperUserImage)
        val userNameText: TextView = itemView.findViewById(R.id.whisperUserNameText)
        val contentText: TextView = itemView.findViewById(R.id.whisperContentText)
        val retweetButton: ImageButton = itemView.findViewById(R.id.retweetButton)
        val retweetCntText: TextView = itemView.findViewById(R.id.retweetCntText)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        val likeCntText: TextView = itemView.findViewById(R.id.likeCntText)
    }
}
