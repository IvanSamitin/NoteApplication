package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import com.example.noteapplication8.R
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.koin.androidx.viewmodel.ext.android.viewModel

class TagsChooseFragment : BottomSheetDialogFragment() {
    private val viewModel by viewModel<NotesViewModel>()
    private val selectedTags = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_tags_choose, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.readAllTags.observe(viewLifecycleOwner) { tags ->
            updateChipGroup(tags)
        }
        setupApplyButton(view)
    }

    private fun updateChipGroup(tags: List<TagsEntity>) {
        val initialSelectedIds = arguments?.getStringArray(ARG_SELECTED_TAG_IDS)?.toSet()
            ?: emptySet() // ✅ Получаем StringArray
        selectedTags.addAll(initialSelectedIds)
        val chipGroup = view?.findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup?.removeAllViews()
        tags.forEach { tag ->
            val chip = createChip(tag)
            chipGroup?.addView(chip)
        }
    }

    private fun createChip(tag: TagsEntity): Chip =
        Chip(requireContext()).apply {
            text = tag.text
            isCheckable = true
            isCheckedIconVisible = true
            isChecked = selectedTags.contains(tag.tagId) // ✅ Используем String
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTags.add(tag.tagId) // ✅ Используем String
                } else {
                    selectedTags.remove(tag.tagId) // ✅ Используем String
                }
            }
        }


    private fun setupApplyButton(view: View) {
        view.findViewById<Button>(R.id.btnApply).setOnClickListener {
            val result = bundleOf("selectedTags" to selectedTags.toTypedArray())
            parentFragmentManager.setFragmentResult("tagsRequestKey", result)
            dismiss()
        }
    }

    companion object {
        private const val ARG_SELECTED_TAG_IDS = "argSelectedTagIds"
        fun newInstance(selectedTags: List<TagsEntity>?): TagsChooseFragment =
            TagsChooseFragment().apply {
                arguments = Bundle().apply {
                    putStringArray( // ✅ Используем StringArray
                        ARG_SELECTED_TAG_IDS,
                        selectedTags?.map { it.tagId }?.toTypedArray()
                    )
                }
            }
    }
}
