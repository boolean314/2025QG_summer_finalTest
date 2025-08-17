// ErrorListAdapter.kt
package com.example.pmp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pmp.R
import com.example.pmp.data.model.BaseErrorData
import com.example.pmp.databinding.ItemErrorListBinding
import com.example.pmp.ui.detail.ErrorDetail

class ErrorListAdapter : ListAdapter<BaseErrorData, ErrorListAdapter.ErrorViewHolder>(ErrorDiffCallback()) {

    // 添加platform变量
    var platform: String = "frontend"

    class ErrorViewHolder(private val binding: ItemErrorListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(error: BaseErrorData) {
            binding.errorType.text = error.errorType
            binding.errorTimestamp.text = formatTimestamp(error.timestamp)
            binding.errorResponsibleName.text = error.name ?: "未指派"

            if (error.name == null) {
                binding.errorResponsibleName.setTextColor(
                    ContextCompat.getColor(binding.errorResponsibleName.context, R.color.red)
                )
            } else {
                binding.errorResponsibleName.setTextColor(
                    ContextCompat.getColor(binding.errorResponsibleName.context, android.R.color.black)
                )
            }

            Glide.with(binding.errorResponsibleAvatar.context)
                .load(error.avatarUrl)
                .into(binding.errorResponsibleAvatar)
        }

        private fun formatTimestamp(timestamp: String): String {
            return timestamp.replace("T", " ").substring(0, 16)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErrorViewHolder {
        val binding = ItemErrorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ErrorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ErrorViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            val error = getItem(position)
            val intent = Intent(holder.itemView.context, ErrorDetail::class.java)
            intent.putExtra("errorId", error.id)
            intent.putExtra("platform", platform) // 使用Adapter中的platform变量
            holder.itemView.context.startActivity(intent)
        }
    }

    // ErrorListAdapter.kt 中的 ErrorDiffCallback 类
    class ErrorDiffCallback : DiffUtil.ItemCallback<BaseErrorData>() {
        override fun areItemsTheSame(oldItem: BaseErrorData, newItem: BaseErrorData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BaseErrorData, newItem: BaseErrorData): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.errorType == newItem.errorType &&
                    oldItem.timestamp == newItem.timestamp &&
                    oldItem.name == newItem.name &&
                    oldItem.avatarUrl == newItem.avatarUrl
        }
    }
}
