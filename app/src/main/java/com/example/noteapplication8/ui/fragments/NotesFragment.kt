package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentNotesBinding
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.ui.adapters.RcNoteAdapter
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.example.noteapplication8.viewmodel.NotesViewModelFactory

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private var allNotes: List<NoteWithTags> = emptyList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            NotesViewModelFactory(requireActivity().application)
        )[NotesViewModel::class.java]

        val adapter = RcNoteAdapter{
            findNavController().navigate(
                R.id.action_mainNotes_to_noteEditFragment,
                bundleOf("note" to it)
            )
        }

        viewModel.readAllNotesWithTags().observe(viewLifecycleOwner){
            allNotes = it
            updateUi(it, adapter)
        }

        binding.apply {
            rcNotes.layoutManager = LinearLayoutManager(requireContext())
            rcNotes.adapter = adapter
        }
    }

    private fun updateUi(notes: List<NoteWithTags>, adapter: RcNoteAdapter){
        adapter.submitList(notes)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotesFragment()
    }
}