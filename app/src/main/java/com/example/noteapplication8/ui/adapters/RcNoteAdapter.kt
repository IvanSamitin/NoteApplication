package com.example.noteapplication8.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapplication8.R
import com.example.noteapplication8.databinding.NoteListBinding
import com.example.noteapplication8.model.entity.NoteWithTags

class RcNoteAdapter(
    private val onItemClick: (NoteWithTags) -> Unit,
) : ListAdapter<NoteWithTags, RcNoteAdapter.NoteViewHolder>(Comparator()) {
    class NoteViewHolder(
        item: View,
    ) : RecyclerView.ViewHolder(item) {
        val binding = NoteListBinding.bind(item)

        fun bind(note: NoteWithTags) =
            with(binding) {
                tvDate.text = note.note.date.toString()

                if (note.note.header
                        .toString()
                        .length >= 60
                ) {
                    val headerType = "${note.note.header.toString().take(60)}..."
                    tvHeader.text = headerType
                } else {
                    tvHeader.text = note.note.header.toString()
                }

                if (note.note.text
                        .toString()
                        .length >= 60
                ) {
                    val textType = "${note.note.text.toString().take(60)}..."
                    tvText.text = textType
                } else {
                    tvText.text = note.note.text.toString()
                }

                if (note.tags.isEmpty()) {
                    tvTagsList.text = "Нету тегов"
                } else {
                    val tagsText = note.tags.joinToString(", ") { it.text }
                    tvTagsList.text = tagsText
                }
            }
    }

    class Comparator : DiffUtil.ItemCallback<NoteWithTags>() {
        override fun areItemsTheSame(
            oldItem: NoteWithTags,
            newItem: NoteWithTags,
        ): Boolean = oldItem.note.noteId == newItem.note.noteId

        override fun areContentsTheSame(
            oldItem: NoteWithTags,
            newItem: NoteWithTags,
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_list, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int,
    ) {
        val note = getItem(position)
        holder.bind(note)

        holder.itemView.setOnClickListener {
            onItemClick(note)
        }
    }
}
