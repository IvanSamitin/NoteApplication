package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentTagEditBinding
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.ui.adapters.RcNoteAdapter
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TagEditFragment : Fragment() {
    private var _binding: FragmentTagEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()
    private var allNotes: List<NoteWithTags> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTagEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RcNoteAdapter {
            findNavController().navigate(
                R.id.action_tagEditFragment2_to_noteEditFragment,
                bundleOf("note" to it),
            )
        }

        val controller = findNavController()
        binding.buttonCancel.setOnClickListener {
            controller.popBackStack()
        }

        val receivedTag = arguments?.getParcelable<TagsEntity>("tag")
        if (receivedTag == null) {
            saveNewTag()
        } else {
            val id = receivedTag.tagId // ✅ tagId теперь String
            binding.tvTagText.setText(receivedTag.text)
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

    private fun readNotes(id: String, adapter: RcNoteAdapter) { // ✅ tagId теперь String
        viewModel.readAllNotesByTags(tagId = id).observe(viewLifecycleOwner) {
            allNotes = it
            updateUi(it, adapter)
        }
    }

    private fun updateUi(
        notes: List<NoteWithTags>,
        adapter: RcNoteAdapter,
    ) {
        adapter.submitList(notes)
    }

    private fun updateCurrentTag(id: String) { // ✅ tagId теперь String
        binding.buttonSave.setOnClickListener {
            val tagText = binding.tvTagText.text.toString().trim()

            when {
                tagText.isEmpty() -> {
                    binding.tvTagText.error = "Название тега не может быть пустым"
                    return@setOnClickListener
                }

                !isValidTag(tagText) -> {
                    binding.tvTagText.error = "Недопустимые символы"
                    return@setOnClickListener
                }

                else -> {
                    viewModel.updateTag(tagId = id, text = tagText)
                    findNavController().popBackStack()
                }
            }
        }
        binding.buttonDelite.setOnClickListener {
            viewModel.deleteTag(
                tag = TagsEntity(
                    tagId = id,
                    text = binding.tvTagText.text.toString(),
                )
            )
            findNavController().popBackStack()
        }
    }

    private fun saveNewTag() {
        binding.buttonSave.setOnClickListener {
            val tagText = binding.tvTagText.text.toString().trim()

            when {
                tagText.isEmpty() -> {
                    binding.tvTagText.error = "Название тега не может быть пустым"
                    return@setOnClickListener
                }

                !isValidTag(tagText) -> {
                    binding.tvTagText.error = "Недопустимые символы"
                    return@setOnClickListener
                }

                else -> {
                    viewModel.createTag(text = tagText)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun isValidTag(text: String): Boolean {
        val regex = Regex("^[a-zA-Zа-яА-Я0-9\\s]+\$")
        return text.matches(regex) && text.isNotBlank()
    }

    override fun onDestroyView() {
        binding.rcTags.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
