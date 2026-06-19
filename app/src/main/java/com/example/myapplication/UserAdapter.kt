package com.example.myapplication

import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private  val userList: List<UserRowData>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): UserViewHolder{
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.user_recycle_row,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder,position: Int) {
        val user = userList[position]

        holder.userNameText.text = user.userName
        holder.followCntText.text = user.followCount.toString()
        holder.followerCntText.text = user.followerCount.toString()
        holder.userImage.setImageResource(user.userImageResId)
    }


    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: android.view.View):
            RecyclerView.ViewHolder(itemView){

            val userImage: android.widget.ImageView =
                itemView.findViewById(R.id.userImage)
            val userNameText: android.widget.TextView =
                itemView.findViewById(R.id.userNameText)
            val followCntText: android.widget.TextView =
                itemView.findViewById(R.id.followCntText)
            val followerCntText: android.widget.TextView =
                itemView.findViewById(R.id.followerCntText)
    }
}