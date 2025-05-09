package com.example.noteapplication8.ui.fragments.firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.noteapplication8.R
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FirebaseSettingFragment : Fragment() {
    private val viewModel by viewModel<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSettingsActions()
    }

    private fun setupSettingsActions() {
        view?.findViewById<View>(R.id.buttonSignOut)?.setOnClickListener {
            viewModel.signOut()
        }
        view?.findViewById<View>(R.id.buttonSync)?.setOnClickListener {
            viewModel.forceSync()
        }

        view?.findViewById<TextView>(R.id.tvEmail)?.text = viewModel.getCurrentUserEmail()
    }
}