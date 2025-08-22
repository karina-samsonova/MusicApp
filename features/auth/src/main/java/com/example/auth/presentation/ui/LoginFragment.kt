package com.example.auth.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.auth.R
import com.example.auth.databinding.FragmentLoginBinding
import com.example.auth.di.AuthComponent
import com.example.auth.di.DaggerAuthComponent
import com.example.auth.domain.AuthState
import com.example.auth.presentation.viewModel.AuthViewModel
import javax.inject.Inject

class LoginFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentLoginBinding? = null
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        initialiseUIElements()
        initialiseObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initialiseUIElements() {
        binding.btnLogin.setOnClickListener {
            if (validateLogin() && validatePassword()) {
                val login = binding.etLogin.getText()
                val password = binding.etPassword.getText()
                viewModel.login(login, password)
            }
        }
        binding.btnForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun initialiseObservers() {
        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Loading -> {}

                is AuthState.Success -> {
                    requireActivity().finish()
                    startActivity(requireActivity().intent)
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

    private fun getComponent(): AuthComponent {

        val component = DaggerAuthComponent.builder()
            .build()

        return component
    }
}