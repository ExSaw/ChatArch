package com.rickrip.dictionary.chat_app.presentation.screen.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rickrip.dictionary.chat_app.presentation.MainViewModel
import com.rickrip.dictionary.chat_app.presentation.event.UiEvent

class LoginFragment: Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener{
                viewModel.sendUiEvent(UiEvent.OnLoginRequest(
                    isUsePrevUser = checkBox.state,
                    login = tvLogin.text,
                    password = tvPassword.text
                ))
            }
        }
    }
}