package com.example.auth.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.auth.R
import com.example.auth.databinding.FragmentRegisterBinding
import com.example.auth.di.AuthComponent
import com.example.auth.di.DaggerAuthComponent
import com.example.auth.domain.AuthState
import com.example.auth.presentation.viewModel.AuthViewModel
import javax.inject.Inject

class RegisterFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        initialiseUIElements()
        initialiseObservers()
    }

    private fun initialiseUIElements() {
        binding.btnRegister.setOnClickListener {
            if (validateLogin() && validatePassword() && validateConfirmPassword()) {
                val login = binding.etLogin.getText()
                val password = binding.etPassword.getText()
                val passwordConfirm = binding.etPasswordConfirm.getText()
                viewModel.register(login, password)
            }
        }
    }

    private fun initialiseObservers() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Loading -> {}

                is AuthState.Success -> {
                    requireActivity().finish()
                    requireActivity().startActivity(requireActivity().intent)
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

    private fun validatePassword(): Boolean {
        if (binding.etPassword.getText() == "") {
            binding.etPassword.setErrorMessage(getString(R.string.enter_password))
            return false
        }
        binding.etPassword.resetErrorMessage()
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        if (binding.etPasswordConfirm.getText() == "") {
            binding.etPasswordConfirm.setErrorMessage(getString(R.string.confirm_password))
            return false
        } else if (binding.etPassword.getText() != binding.etPasswordConfirm.getText()) {
            binding.etPasswordConfirm.setErrorMessage(getString(R.string.passwords_do_not_match))
            return false
        }
        binding.etPasswordConfirm.resetErrorMessage()
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