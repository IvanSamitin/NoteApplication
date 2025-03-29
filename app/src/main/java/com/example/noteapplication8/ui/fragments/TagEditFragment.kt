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
import com.example.noteapplication8.databinding.FragmentNoteEditBinding
import com.example.noteapplication8.databinding.FragmentTagEditBinding
import com.example.noteapplication8.databinding.FragmentTagsBinding
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.ui.adapters.RcNoteAdapter
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.example.noteapplication8.viewmodel.NotesViewModelFactory

class TagEditFragment : Fragment() {
    private var _binding: FragmentTagEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel
    private var allNotes: List<NoteWithTags> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            NotesViewModelFactory(requireActivity().application)
        )[NotesViewModel::class.java]

        val adapter = RcNoteAdapter {
            findNavController().navigate(
                R.id.action_tagEditFragment2_to_noteEditFragment,
                bundleOf("note" to it)
            )
        }

        val controller = findNavController()
        binding.buttonCancel.setOnClickListener {
            controller.popBackStack()
        }

        val receivedTag = arguments?.getParcelable<TagsEntity>("tag")

        if (receivedTag == null){
            saveNewTag()
        } else {
            val id: Long
            receivedTag.let{ tag ->
                id = tag.tagId
                binding.tvTagText.setText(tag.text)
            }
            updateCurrentTag(id)
            readNotes(id, adapter)
        }

        binding.buttonCancel.setOnClickListener {
            controller.popBackStack()
        }

        binding.apply {
            rcTags.layoutManager = LinearLayoutManager(requireContext())
            rcTags.adapter = adapter
        }
    }

    private fun readNotes(id: Long, adapter: RcNoteAdapter){
        viewModel.readAllNotesByTags(tagId = id).observe(viewLifecycleOwner){
            allNotes = it
            updateUi(it, adapter)
        }
    }

    private fun updateUi(notes: List<NoteWithTags>, adapter: RcNoteAdapter) {
        adapter.submitList(notes)
    }

    private fun updateCurrentTag(id: Long) {
        binding.buttonSave.setOnClickListener {
            viewModel.updateTag(tag = TagsEntity(
                tagId = id,
                text = binding.tvTagText.text.toString()))
            findNavController().popBackStack()
        }
        binding.buttonDelite.setOnClickListener {
            viewModel.deleteTag(tag = TagsEntity(
                tagId = id,
                text = binding.tvTagText.text.toString()))
            findNavController().popBackStack()
        }
    }

    private fun saveNewTag() {
        binding.buttonSave.setOnClickListener {
            viewModel.createTag(tag = TagsEntity(text = binding.tvTagText.text.toString()))
            findNavController().popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagEditFragment()
    }
}