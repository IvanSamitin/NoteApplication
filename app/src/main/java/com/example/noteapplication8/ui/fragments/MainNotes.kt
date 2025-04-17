package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentMainNotesBinding
import com.example.noteapplication8.ui.adapters.VpNoteAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainNotes : Fragment() {
    private var _binding: FragmentMainNotesBinding? = null
    private val binding get() = _binding!!
    private var tabLayoutMediator: TabLayoutMediator? = null

    private val fList =
        listOf(
            NotesFragment.newInstance(),
            TagsFragment.newInstance(),
        )

    private val tList =
        listOf(
            "Заметки",
            "Теги",
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = VpNoteAdapter(this, fList)
        binding.vp.adapter = adapter
        val controller = findNavController()

        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
                tab.text = tList[position]
            }.apply { attach() }

        binding.buttonCreate.setOnClickListener {
            when (binding.vp.currentItem) {
                0 -> controller.navigate(R.id.action_mainNotes_to_noteEditFragment)
                1 -> controller.navigate(R.id.action_mainNotes_to_tagEditFragment2)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding.vp.adapter = null
        _binding = null
    }
}
