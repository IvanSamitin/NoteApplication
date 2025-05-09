package com.example.noteapplication8.ui.fragments

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.collections.toLongArray

class NoteEditFragment : Fragment() {
    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()

    private var tags: Array<String>? = null
    private val selectedDate = Calendar.getInstance()
    private var currentSelectedTagIds = mutableSetOf<String>()

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
            val id: String = receivedNote.note.noteId // ✅ noteId теперь String
            binding.tvDate.setText(receivedNote.note.date)
            binding.tvHeader.setText(receivedNote.note.header)
            binding.tvText.setText(receivedNote.note.text)
            tags = receivedNote.tags.map { it.tagId }.toTypedArray()
            if (receivedNote.tags.isNotEmpty()) {
                binding.containerTags.visibility = View.VISIBLE
                val tagsText = receivedNote.tags.joinToString(", ") { it.text }
                binding.tvAllTags.setText(tagsText)
            }
            updateCurrentNote(id)
        }

        binding.buttonCancel.setOnClickListener {
            controller.popBackStack()
        }

        binding.buttonTags.setOnClickListener {
            val dialog = TagsChooseFragment.newInstance(
                currentSelectedTagIds.map {
                    TagsEntity(
                        tagId = it,
                        text = ""
                    )
                }
            )
            dialog.show(parentFragmentManager, "tagsDialog")
        }

        parentFragmentManager.setFragmentResultListener("tagsRequestKey", this) { key, bundle ->
            tags = bundle.getStringArray("selectedTags")
            updateNoteTags(tags)
        }

        binding.tvDate.setOnClickListener {
            showMaterialDatePicker()
        }
    }

    private fun showMaterialDatePicker() {
        val picker = MaterialDatePicker.Builder
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

    private fun updateNoteTags(tagIds: Array<String>?) {
        tagIds?.let {
            currentSelectedTagIds.clear()
            currentSelectedTagIds.addAll(it.toSet())
            tags = it
        }

        viewModel.getTagsByIds(tagIds).observe(viewLifecycleOwner) { tags ->
            if (tags.isNotEmpty()) {
                binding.containerTags.visibility = View.VISIBLE
                val tagsText = tags.joinToString(", ") { it.text }
                binding.tvAllTags.setText(tagsText)
                binding.containerTags
            }
        }
    }

    private fun updateCurrentNote(id: String) {
        binding.buttonSave.setOnClickListener {
            val date = binding.tvDate.text.toString()
            val header = binding.tvHeader.text.toString()
            val text = binding.tvText.text.toString()
            when {
                header.isEmpty() -> {
                    binding.tvHeader.error = "Заголовок не может быть пустым"
                    return@setOnClickListener
                }

                !isValidHeader(header) -> {
                    binding.tvHeader.error = "Недопустимые символы в заголовке"
                    return@setOnClickListener
                }

                text.isEmpty() -> {
                    binding.tvText.error = "Текст не может быть пустым"
                    return@setOnClickListener
                }

                else -> {
                    if (tags != null) {
                        viewModel.updateNoteWithTags(id, date, header, text, tags!!)
                    } else {
                        viewModel.updateNoteWithoutTags(id, date, header, text)
                    }
                    findNavController().popBackStack()
                }
            }
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
            when {
                header.isEmpty() -> {
                    binding.tvHeader.error = "Заголовок не может быть пустым"
                    return@setOnClickListener
                }

                !isValidHeader(header) -> {
                    binding.tvHeader.error = "Недопустимые символы в заголовке"
                    return@setOnClickListener
                }

                text.isEmpty() -> {
                    binding.tvText.error = "Текст не может быть пустым"
                    return@setOnClickListener
                }

                else -> {
                    if (tags != null) {
                        viewModel.createNoteWithTags(date, header, text, tags!!)
                    } else {
                        viewModel.createNoteWithoutTag(date, header, text)
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun isValidHeader(header: String): Boolean {
        val regex = Regex("^[a-zA-Zа-яА-Я0-9\\s.,!?()-]+\$")
        return header.matches(regex)
    }


    private fun isValidText(text: String): Boolean {
        return text.isNotBlank()
    }

    override fun onDestroyView() {
        parentFragmentManager.clearFragmentResultListener("tagsRequestKey")
        _binding = null
        super.onDestroyView()
    }
}
