package com.example.settings.presentation.ui

import android.app.Dialog
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.settings.R
import com.example.settings.presentation.viewModel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LogoutDialogFragment : DialogFragment() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBackground = ResourcesCompat.getDrawable(
            resources,
            com.example.design_system.R.drawable.rounded_dialog,
            null
        )
        return MaterialAlertDialogBuilder(requireContext())
            .setBackground(dialogBackground)
            .setTitle(R.string.logout_title)
            .setPositiveButton(R.string.exit) { _, _ ->
                viewModel.logout()
                requireActivity().finish()
                startActivity(requireActivity().intent)
            }
            .setNegativeButton(R.string.close, null)
            .create()
    }
}