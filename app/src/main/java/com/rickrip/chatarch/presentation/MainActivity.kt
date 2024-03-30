package com.rickrip.dictionary.chat_app.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rickrip.dictionary.R
import com.rickrip.dictionary.chat_app.core.util.IDispatchersProvider
import com.rickrip.dictionary.chat_app.presentation.event.SystemMessage
import com.rickrip.dictionary.chat_app.presentation.router.ScreenRouter
import com.rickrip.dictionary.chat_app.presentation.screen.chat.ChatFragment
import com.rickrip.dictionary.chat_app.presentation.screen.contacts.ContactsFragment
import com.rickrip.dictionary.chat_app.presentation.screen.login.LoginFragment
import com.rickrip.dictionary.chat_app.presentation.state.Stage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var dispatchers: IDispatchersProvider
    @Inject
    lateinit var screenRouter: ScreenRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(dispatchers.default) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stageState.collectLatest {
                    handleStage(it)
                }
            }
        }
        lifecycleScope.launch(dispatchers.default) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.systemMessagesBus.collectLatest {
                    handleEvent(it)
                }
            }
        }
    }

    private fun handleStage(stage: Stage) {
        when (stage) {
            is Stage.AuthStage -> {
                screenRouter.openFragment<LoginFragment>(
                    activity = this,
                    containerId = binding.fragmentContaner,
                    isAddToBackStack = false
                )
            }
            is Stage.ChatStage -> {
                screenRouter.openFragment<ContactsFragment>(
                    activity = this,
                    containerId = binding.fragmentContaner,
                    isAddToBackStack = true
                )
            }
            is Stage.ContactsStage -> {
                screenRouter.openFragment<ChatFragment>(
                    activity = this,
                    containerId = binding.fragmentContaner,
                    isAddToBackStack = true
                )
            }
        }
    }

    private fun handleEvent(systemMessage: SystemMessage) {
        val message = when (systemMessage) {
            SystemMessage.LOADING -> {
                getString(R.string.loading_message)
            }

            SystemMessage.NO_CONNECTION -> {
                getString(R.string.no_connection)
            }

            else -> {
                getString(R.string.other_message)
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}