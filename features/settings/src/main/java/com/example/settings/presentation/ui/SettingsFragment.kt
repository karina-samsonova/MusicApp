package com.example.settings.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.settings.R
import com.example.settings.databinding.FragmentSettingsBinding
import com.example.settings.domain.model.ThemeMode
import com.example.settings.domain.model.ThemePrefs
import com.example.settings.presentation.viewModel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialiseUIElements() {
        setChecked(viewModel.getCurrentTheme())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        binding.layoutLightTheme.setOnClickListener {
            setTheme(ThemeMode.LIGHT)
        }
        binding.layoutDarkTheme.setOnClickListener {
            setTheme(ThemeMode.DARK)
        }
        binding.layoutSystemTheme.setOnClickListener {
            setTheme(ThemeMode.SYSTEM)
        }
    }

    private fun showLogoutDialog() {
        LogoutDialogFragment().show(parentFragmentManager, "logout_dialog")
    }

    private fun setChecked(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.LIGHT ->
                binding.checkBoxLightTheme.setImageResource(com.example.design_system.R.drawable.checked)

            ThemeMode.DARK ->
                binding.checkBoxDarkTheme.setImageResource(com.example.design_system.R.drawable.checked)

            ThemeMode.SYSTEM ->
                binding.checkBoxSystemTheme.setImageResource(com.example.design_system.R.drawable.checked)
        }
    }

    private fun setTheme(themeMode: ThemeMode) {
        when (viewModel.getCurrentTheme()) {
            ThemeMode.LIGHT ->
                binding.checkBoxLightTheme.setImageResource(com.example.design_system.R.drawable.unchecked)

            ThemeMode.DARK ->
                binding.checkBoxDarkTheme.setImageResource(com.example.design_system.R.drawable.unchecked)

            ThemeMode.SYSTEM ->
                binding.checkBoxSystemTheme.setImageResource(com.example.design_system.R.drawable.unchecked)
        }
        setChecked(themeMode)
        viewModel.setTheme(themeMode)
        when (themeMode) {
            ThemeMode.SYSTEM ->
                ThemePrefs.setThemeMode(
                    requireContext(),
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )

            ThemeMode.DARK ->
                ThemePrefs.setThemeMode(requireContext(), AppCompatDelegate.MODE_NIGHT_YES)

            ThemeMode.LIGHT ->
                ThemePrefs.setThemeMode(requireContext(), AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}