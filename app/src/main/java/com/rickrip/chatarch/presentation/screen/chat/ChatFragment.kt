package com.rickrip.dictionary.chat_app.presentation.screen.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.presentation.MainViewModel
import com.rickrip.dictionary.chat_app.presentation.event.SystemMessage
import com.rickrip.dictionary.chat_app.presentation.event.UiEvent
import com.rickrip.dictionary.chat_app.presentation.screen.chat.list.ChatListAdapter
import com.rickrip.dictionary.chat_app.presentation.state.Stage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    @Inject
    private lateinit var dispatchers: IDispatchersProvider

    private val rvChatListAdapter = ChatListAdapter(
        onItemClick = {
            /*nothing to do*/
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(dispatchers.default) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stageState
                    .collectLatest { stage ->
                        (stage as? Stage.ChatStage)?.let { chatStage ->
                            chatViewModel.getMessages(
                                userId = chatStage.userId,
                                contactId = chatStage.contactId
                            ).collect { pagingData ->
                                rvChatListAdapter.submitData(pagingData)
                            }
                        }
                    }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(dispatchers.default) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.systemMessagesBus
                    .filter { it == SystemMessage.NEED_UPDATE }
                    .collectLatest {
                        rvChatListAdapter.refresh()
                    }
            }
        }
        binding.apply {
            rvChatList.adapter = rvChatListAdapter
            btnSendMessage.setOnClickListener {
                viewModel.sendUiEvent(
                    UiEvent.OnSendMessage(tvMessage.text)
                )
            }
        }
    }
}