package com.example.noteapplication8.ui.fragments

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.databinding.FragmentNoteEditBinding
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class NoteEditFragment : Fragment() {
    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()
    private var tags: LongArray? = null
    private val selectedDate = Calendar.getInstance()
    private var currentSelectedTagIds = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()

        arguments?.getParcelable<NoteWithTags>("note")?.tags?.let { tags ->
            currentSelectedTagIds.addAll(tags.map { it.tagId })
        }

        val receivedNote = arguments?.getParcelable<NoteWithTags>("note")

        if (receivedNote == null) {
            saveNewNote()
        } else {
            val id: Long
            receivedNote.let { note ->
                id = note.note.noteId
                binding.tvDate.setText(note.note.date)
                binding.tvHeader.setText(note.note.header)
                binding.tvText.setText(note.note.text)
                if (note.tags.isNotEmpty()) {
                    binding.containerTags.visibility = View.VISIBLE
                    val tagsText = note.tags.joinToString(", ") { it.text }
                    binding.tvAllTags.setText(tagsText)
                }
            }
            updateCurrentNote(id)
        }

        binding.buttonCancel.setOnClickListener {
            controller.popBackStack()
        }

        binding.buttonTags.setOnClickListener {
            val dialog =
                TagsChooseFragment.newInstance(
                    currentSelectedTagIds.map { TagsEntity(it, "") },
                )
            dialog.show(parentFragmentManager, "tagsDialog")
        }

        parentFragmentManager.setFragmentResultListener("tagsRequestKey", this) { key, bundle ->
            tags = bundle.getLongArray("selectedTags")!!

            updateNoteTags(tags)
        }

        binding.tvDate.setOnClickListener {
            showMaterialDatePicker()
        }
    }


    private fun showMaterialDatePicker() {
        val picker =
            MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Выберите дату")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        picker.addOnPositiveButtonClickListener { selection ->
            selection?.let {
                selectedDate.timeInMillis = it
                updateDateLabel()
            }
        }
        picker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
    }

    private fun updateDateLabel() {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding.tvDate.setText(sdf.format(selectedDate.time))
    }

    private fun updateNoteTags(tagIds: LongArray?) {
        viewModel.getTagsByIds(tagIds).observe(viewLifecycleOwner) { tags ->
            if (tags.isNotEmpty()) {
                binding.containerTags.visibility = View.VISIBLE
                val tagsText = tags.joinToString(", ") { it.text }
                binding.tvAllTags.setText(tagsText.toString())
            }
        }
    }

    private fun updateCurrentNote(id: Long) {
        binding.buttonSave.setOnClickListener {
            val date = binding.tvDate.text.toString()
            val header = binding.tvHeader.text.toString()
            val text = binding.tvText.text.toString()

            if (tags != null) {
                viewModel.updateNoteWithTags(id, date, header, text, tags!!)
            } else {
                viewModel.updateNoteWithoutTags(id, date, header, text)
            }
            findNavController().popBackStack()
        }

        binding.buttonDelite.setOnClickListener {
            viewModel.deleteNote(id)
            findNavController().popBackStack()
        }
    }

    private fun saveNewNote() {
        binding.buttonSave.setOnClickListener {
            val date = binding.tvDate.text.toString()
            val header = binding.tvHeader.text.toString()
            val text = binding.tvText.text.toString()

            if (tags != null) {
                viewModel.createNoteWithTags(date, header, text, tags!!)
            } else {
                viewModel.createNoteWithoutTag(date, header, text)
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        parentFragmentManager.clearFragmentResultListener("tagsRequestKey")
        _binding = null
        super.onDestroyView()
    }
}
