package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.noteapplication8.R
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.example.noteapplication8.viewmodel.NotesViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TagsChooseFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: NotesViewModel
    private val selectedTags = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tags_choose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            NotesViewModelFactory(requireActivity().application)
        )[NotesViewModel::class.java]

//        val myDataList: ArrayList<TagsEntity>? = arguments?.getParcelableArrayList(ARG_SOME_DATA)

        viewModel.readAllTags().observe(viewLifecycleOwner) { tags ->
            updateChipGroup(tags)
        }
        setupApplyButton(view)
    }

    private fun updateChipGroup(tags: List<TagsEntity>) {
        val chipGroup = view?.findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup?.removeAllViews()

        tags.forEach { tag ->
            val chip = createChip(tag)
            chipGroup?.addView(chip)
        }
    }

    private fun createChip(tag: TagsEntity): Chip {
        return Chip(requireContext()).apply {
            text = tag.text
            isCheckable = true
            isCheckedIconVisible = true

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTags.add(tag.tagId)
                } else {
                    selectedTags.remove(tag.tagId)
                }
            }
        }
    }

    private fun setupApplyButton(view: View) {
        view.findViewById<Button>(R.id.btnApply).setOnClickListener {
            val result = bundleOf("selectedTags" to selectedTags.toLongArray())
            parentFragmentManager.setFragmentResult("tagsRequestKey", result)
            dismiss()
        }
    }

    companion object {
        private const val ARG_SOME_DATA = "argSomeData"

        fun newInstance(someData: List<TagsEntity>?): TagsChooseFragment {
            val fragment = TagsChooseFragment()
            val args = Bundle().apply {
                putParcelable(ARG_SOME_DATA, someData as Parcelable?)
            }
            fragment.arguments = args
            return fragment
        }
    }
}