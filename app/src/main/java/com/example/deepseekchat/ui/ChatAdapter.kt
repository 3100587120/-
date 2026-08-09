package com.example.deepseekchat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            binding.tvContent.text = message.content
            binding.root.setBackgroundColor(
                if (message.role == "user") 0xFFE3F2FD.toInt() else 0xFFFFFFFF.toInt()
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DisplayMessage>() {
        override fun areItemsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) = oldItem === newItem
        override fun areContentsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) =
            oldItem.role == newItem.role && oldItem.content == newItem.content
    }
}
