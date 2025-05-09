package com.example.noteapplication8.ui.fragments.firebase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentLoginBinding
import com.example.noteapplication8.utils.Constants.LOGIN
import com.example.noteapplication8.utils.Constants.PASSWORD
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonlogin.setOnClickListener {
            LOGIN = binding.tvLogin.text.toString()
            PASSWORD = binding.tvPassword.text.toString()
            viewModel.login() {
                Log.d("checkData", "ti voshel")
                findNavController().popBackStack()
            }
        }

        binding.backGroup.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}