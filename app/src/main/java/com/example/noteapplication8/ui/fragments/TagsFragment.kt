package com.example.noteapplication8.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.FragmentTagsBinding
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.example.noteapplication8.viewmodel.NotesViewModelFactory
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView

class TagsFragment : Fragment() {
    private var _binding: FragmentTagsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTagsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(
                this,
                NotesViewModelFactory(requireActivity().application),
            )[NotesViewModel::class.java]

        viewModel.readAllTags().observe(viewLifecycleOwner) { tags ->
            updateFlexboxLayout(tags)
        }
    }

    private fun createTagCard(tag: TagsEntity): MaterialCardView {
        val cardView =
            layoutInflater.inflate(
                R.layout.item_tag,
                binding.flexboxLayout,
                false,
            ) as MaterialCardView

        val textView = cardView.findViewById<TextView>(R.id.tagText)
        textView.text = tag.text

        val layoutParams =
            FlexboxLayout
                .LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    setMargins(4, 4, 4, 4)
                }

        cardView.layoutParams = layoutParams

        cardView.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainNotes_to_tagEditFragment2,
                bundleOf("tag" to tag),
            )
        }
        return cardView
    }

    private fun updateFlexboxLayout(tags: List<TagsEntity>) {
        val flexboxLayout = binding.flexboxLayout
        flexboxLayout.removeAllViews() // Очищаем предыдущие элементы

        tags.forEach { tag ->
            val cardView = createTagCard(tag)
            flexboxLayout.addView(cardView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagsFragment()
    }

    override fun onDestroyView() {
        val flexboxLayout = binding.flexboxLayout
        for (i in 0 until flexboxLayout.childCount) {
            val child = flexboxLayout.getChildAt(i)
            if (child is MaterialCardView) {
                child.setOnClickListener(null) // Очистить OnClickListener
                // Очистить другие обратные вызовы или ссылки, если они есть
            }
        }
        flexboxLayout.removeAllViews()
        _binding = null
        super.onDestroyView()
    }
}
