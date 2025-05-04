package com.example.noteapplication8.ui.fragments.firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentFirebaseMainBinding
import com.example.noteapplication8.databinding.FragmentFirebaseSettingBinding
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FirebaseMainFragment : Fragment() {
    private var _binding: FragmentFirebaseMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()


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
        updateUi()
    }

    private fun updateUi(){
        if (viewModel.isUserAuthenticated()){
            binding.root.removeAllViews()
            LayoutInflater.from(context).inflate(R.layout.fragment_firebase_setting, binding.root)
            setupSettingsActions()
        } else{
            binding.root.removeAllViews()
            LayoutInflater.from(context).inflate(R.layout.fragment_guest, binding.root)
            setupGuestActions()
        }
    }

    private fun setupSettingsActions(){
        view?.findViewById<View>(R.id.buttonSignOut)?.setOnClickListener {
            viewModel.signOut()
            updateUi()
        }

    }

    private fun setupGuestActions() {
        view?.findViewById<View>(R.id.buttonSignIn)?.setOnClickListener {
            findNavController().navigate(R.id.action_mainNotes_to_loginFragment)
        }

        view?.findViewById<View>(R.id.buttonSignUp)?.setOnClickListener {
            findNavController().navigate(R.id.action_mainNotes_to_registerFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FirebaseMainFragment()
    }
}