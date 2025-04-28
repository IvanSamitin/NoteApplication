package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentTagsBinding
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.viewModel

class TagsFragment : Fragment() {
    private var _binding: FragmentTagsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NotesViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.readAllTags().observe(viewLifecycleOwner) { tags ->
            updateChipGroup(tags)
        }
    }

    private fun createTagChip(tag: TagsEntity): Chip {
        return Chip(requireContext()).apply {
            text = tag.text
            isClickable = true
            setOnClickListener {
                navigateToTagEditFragment(tag)
            }
        }
    }

    private fun navigateToTagEditFragment(tag: TagsEntity) {
        findNavController().navigate(
            R.id.action_mainNotes_to_tagEditFragment2,
            bundleOf("tag" to tag)
        )
    }

    private fun updateChipGroup(tags: List<TagsEntity>) {
        val chipGroup = binding.chipGroup
        chipGroup.removeAllViews() // Очищаем предыдущие элементы

        tags.forEach { tag ->
            val chip = createTagChip(tag)
            chipGroup.addView(chip)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagsFragment()
    }

    override fun onDestroyView() {
        val chipGroup = binding.chipGroup
        for (i in 0 until chipGroup.childCount) {
            val child = chipGroup.getChildAt(i)
            if (child is Chip) {
                child.setOnClickListener(null) // Очистить OnClickListener
            }
        }
        chipGroup.removeAllViews()
        _binding = null
        super.onDestroyView()
    }
}