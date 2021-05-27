package com.qltech.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.NavHostFragment

class XNavHostFragment : NavHostFragment(), IBackPressed {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = CoordinatorLayout(inflater.context)
        // When added via XML, this has no effect (since this CoordinatorLayout is given the ID
        // automatically), but this ensures that the View exists as part of this Fragment's View
        // hierarchy in cases where the NavHostFragment is added programmatically as is required
        // for child fragment transactions
        layout.id = id
        return layout
    }

    override fun onBackPressed(): Boolean {
        val onBackPressed =
            childFragmentManager.findFragmentById(R.id.fragment_container) as? IBackPressed
        return onBackPressed?.onBackPressed() ?: false
    }
}