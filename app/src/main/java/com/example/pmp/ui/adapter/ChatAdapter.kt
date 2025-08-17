package com.example.pmp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pmp.R
import com.example.pmp.data.model.ChatMessage
import com.example.pmp.data.model.GlobalData
import com.example.pmp.databinding.LeftChatMsgItemBinding
import com.example.pmp.databinding.RightChatMsgItemBinding
import com.example.pmp.viewModel.MissionsVM

class ChatAdapter(
    private val chatList: List<ChatMessage>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isUser) TYPE_RIGHT else TYPE_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RIGHT) {
            val binding = RightChatMsgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightViewHolder(binding)
        } else {
            val binding = LeftChatMsgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = chatList[position]
        if (holder is RightViewHolder) {
            holder.binding.sendMsg.text = msg.content
            Glide
                .with(holder.itemView.context)
                .load(GlobalData.userInfo?.avatar)
                .placeholder(R.drawable.coach)
                .error(R.drawable.coach)
                .into(holder.binding.userAvatarImage)
        } else if (holder is LeftViewHolder) {
            val markDownText = msg.content.trimIndent()
            holder.binding.receiveMsg.setMDText(markDownText)

        }
    }

    class RightViewHolder(val binding: RightChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root) {}
    class LeftViewHolder(val binding: LeftChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root)
}