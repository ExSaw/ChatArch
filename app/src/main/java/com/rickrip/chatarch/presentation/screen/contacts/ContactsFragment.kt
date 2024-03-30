package com.rickrip.dictionary.chat_app.presentation.screen.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rickrip.dictionary.chat_app.presentation.MainViewModel

class ContactsFragment: Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * * functions:
         * * addContact
         * * selectContact
         */
    }
}