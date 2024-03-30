package com.rickrip.dictionary.chat_app.presentation.router

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class ScreenRouter {
    inline fun <reified F : Fragment> openFragment(
        activity: FragmentActivity,
        @LayoutRes containerId: Int,
        isAddToBackStack: Boolean,
    ) {
        activity.supportFragmentManager.commit {
            if (isAddToBackStack) {
                addToBackStack(F::class.java.simpleName)
            }
            replace<F>(containerId, F::class.java.name)
        }
    }
}