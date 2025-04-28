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
import com.example.noteapplication8.ui.fragments.firebase.FirebaseMainFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainNotes : Fragment() {
    private var _binding: FragmentMainNotesBinding? = null
    private val binding get() = _binding!!
    private var tabLayoutMediator: TabLayoutMediator? = null

    private val fragmentList = listOf(
        NotesFragment.newInstance(),
        TagsFragment.newInstance(),
        FirebaseMainFragment.newInstance()
    )

    private val tabTitles = listOf(
        "Заметки",
        "Теги",
        "Синхронизация"
    )

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

        val adapter = VpNoteAdapter(this, fragmentList)
        binding.vp.adapter = adapter

        val controller = findNavController()

        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
                tab.text = tabTitles[position]
            }.apply { attach() }

        binding.vp.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Управляем видимостью кнопки buttonCreate в зависимости от позиции
                binding.buttonCreate.visibility = if (position == 2) View.GONE else View.VISIBLE
            }
        })

        binding.buttonCreate.setOnClickListener {
            when (binding.vp.currentItem) {
                0 -> controller.navigate(R.id.action_mainNotes_to_noteEditFragment)
                1 -> controller.navigate(R.id.action_mainNotes_to_tagEditFragment2)
                2 -> binding.buttonCreate.visibility = View.INVISIBLE
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
