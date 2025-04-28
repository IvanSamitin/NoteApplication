package com.example.noteapplication8.ui.fragments.firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentFirebaseMainBinding

class FirebaseMainFragment : Fragment() {
    private var _binding: FragmentFirebaseMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirebaseMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.removeAllViews()
//        LayoutInflater.from(context).inflate(R.layout.fragment_guest, binding.root)
        LayoutInflater.from(context).inflate(R.layout.fragment_firebase_setting, binding.root)
        setupGuestActions()
    }

    private fun setupGuestActions() {
        view?.findViewById<View>(R.id.buttonSignIn)?.setOnClickListener {
            // Переход к экрану входа
            findNavController().navigate(R.id.action_mainNotes_to_loginFragment)
        }

        view?.findViewById<View>(R.id.buttonSignUp)?.setOnClickListener {
            // Переход к экрану регистрации
            findNavController().navigate(R.id.action_mainNotes_to_registerFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FirebaseMainFragment()
    }
}