package com.rickrip.dictionary.chat_app.presentation.screen.chat.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rickrip.dictionary.chat_app.domain.model.MessageModel

class ChatListAdapter(
    private val onItemClick: (MessageModel) -> Unit
): PagingDataAdapter<MessageModel, ChatListViewHolders>(
    DifferCallbackHotelsAdapter()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolders {
        return ChatListViewHolders.MessageViewHolder(
            binding = ItemMessageBinding.inflate(LayoutInflater,parent,false),
            onClick = onItemClick
        )
    }

    override fun onBindViewHolder(holder: ChatListViewHolders, position: Int) {
        when (holder) {
            is ChatListViewHolders.MessageViewHolder -> holder.bind(getItem(position))
            else -> ...
        }
    }
}

class DifferCallbackHotelsAdapter: DiffUtil.ItemCallback<MessageModel>() {

    override fun areItemsTheSame(
        oldItem: MessageModel,
        newItem: MessageModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MessageModel,
        newItem: MessageModel
    ): Boolean {
        return oldItem == newItem
    }
}