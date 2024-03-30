package com.rickrip.dictionary.chat_app.presentation.screen.chat.list

import androidx.recyclerview.widget.RecyclerView
import com.rickrip.dictionary.chat_app.domain.model.MessageModel

sealed class ChatListViewHolders(
    binding: ViewBinding
): RecyclerView.ViewHolder(binding.root) {

    class MessageViewHolder(
        private val binding: ItemMessageBinding,
        private val onClick: (MessageModel) -> Unit
    ) : ChatListViewHolders(binding) {
        fun bind(item: MessageModel) {
           binding.apply {
               root.setOnClickListener { onClick(item) }
               tvMessage.text = item.message
               tvDate.text = item.date
           }
        }
    }
}