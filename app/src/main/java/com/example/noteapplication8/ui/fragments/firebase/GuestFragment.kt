package com.example.noteapplication8.ui.fragments.firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R

class GuestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_guest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGuestActions()
    }

    private fun setupGuestActions() {
        view?.findViewById<View>(R.id.buttonSignIn)?.setOnClickListener {
            findNavController().navigate(R.id.action_mainNotes_to_loginFragment)
        }

        view?.findViewById<View>(R.id.buttonSignUp)?.setOnClickListener {
            findNavController().navigate(R.id.action_mainNotes_to_registerFragment)
        }
    }
}