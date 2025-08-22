package com.example.auth.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.auth.R
import com.example.auth.databinding.FragmentResetPasswordBinding
import com.example.auth.di.AuthComponent
import com.example.auth.di.DaggerAuthComponent
import com.example.auth.domain.AuthState
import com.example.auth.presentation.viewModel.AuthViewModel
import javax.inject.Inject

class ResetPasswordFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    val viewModel: AuthViewModel by viewModels()

    override fun onAttach(context: Context) {
        val component = getComponent()
        component.inject(this)
        component.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResetPasswordBinding.bind(view)

        initialiseUIElements()
        initialiseObservers()
    }

    private fun initialiseUIElements() {
        binding.btnResetPassword.setOnClickListener {
            if (validateLogin()) {
                val login = binding.etLogin.getText()
                viewModel.resetPassword(login)
            }
        }
    }

    private fun initialiseObservers() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Loading -> {}

                is AuthState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.password_reset_email_sent,
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().popBackStack()
                }

                is AuthState.Error -> {
                    val errorMessage =
                        it.message ?: getString(R.string.unknown_error)
                    binding.textErrorMessage.text = errorMessage
                    binding.textErrorMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateLogin(): Boolean {
        if (binding.etLogin.getText() == "") {
            binding.etLogin.setErrorMessage(getString(R.string.enter_email))
            return false
        }
        binding.etLogin.resetErrorMessage()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): AuthComponent {

        val component = DaggerAuthComponent.builder()
            .build()

        return component
    }
}