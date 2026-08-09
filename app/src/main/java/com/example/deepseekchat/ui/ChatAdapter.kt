package com.example.deepseekchat.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.deepseekchat.R
import com.example.deepseekchat.databinding.ItemMessageBinding
import com.example.deepseekchat.model.DisplayMessage

class ChatAdapter : ListAdapter<DisplayMessage, ChatAdapter.MessageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: DisplayMessage) {
            binding.tvRole.text = if (message.role == "user") "我" else "DeepSeek"

            // 设置内容
            binding.tvContent.text = message.content

            // 设置气泡背景色
            binding.tvContent.background = if (message.role == "user") {
                binding.root.context.getDrawable(R.drawable.bubble_user)
            } else {
                binding.root.context.getDrawable(R.drawable.bubble_assistant)
            }

            // 处理思考过程
            if (message.role == "assistant" && message.reasoning.isNotBlank()) {
                binding.layoutReasoning.visibility = View.VISIBLE
                binding.tvReasoning.text = message.reasoning
            } else {
                binding.layoutReasoning.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DisplayMessage>() {
        override fun areItemsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage): Boolean {
            return oldItem.role == newItem.role &&
                    oldItem.content == newItem.content &&
                    oldItem.reasoning == newItem.reasoning
        }
    }
}
