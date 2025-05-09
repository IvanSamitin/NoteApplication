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
import com.example.noteapplication8.databinding.FragmentRegisterBinding
import com.example.noteapplication8.utils.Constants.LOGIN
import com.example.noteapplication8.utils.Constants.PASSWORD
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonRegister.setOnClickListener {
            LOGIN = binding.tvLogin.text.toString()
            PASSWORD = binding.tvPassword.text.toString()
            viewModel.register {
                Log.d("checkData", "ti zaregan")
                findNavController().popBackStack()
            }
        }
        binding.backGroup.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}