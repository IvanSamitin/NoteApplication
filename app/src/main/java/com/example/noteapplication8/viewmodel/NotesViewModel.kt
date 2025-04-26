package com.example.noteapplication8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.model.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    fun createNoteWithoutTag(date: String, header: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(date = date, header = header, text = text)
            repository.createNoteWithoutTag(note = note)
        }
    }

    fun createNoteWithTags(date: String, header: String, text: String, tagIds: LongArray) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(date = date, header = header, text = text)
            repository.createNoteWithTags(note, tagIds)
        }
    }

    fun updateNoteWithoutTags(noteId: Long, date: String, header: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(noteId = noteId, date = date, header = header, text = text)
            repository.updateNote(note = note)
        }
    }

    fun updateNoteWithTags(
        noteId: Long,
        date: String,
        header: String,
        text: String,
        tagIds: LongArray
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(noteId = noteId, date = date, header = header, text = text)
            repository.updateNoteWithTags(note, tagIds)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNoteWithoutTag(noteId)
        }
    }

    fun deleteTag(tag: TagsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTag(tag = tag)
        }
    }

    fun createTag(text: String) {
        viewModelScope.launch {
            repository.createTag(TagsEntity(text = text))
        }
    }

    fun updateTag(tagId: Long, text: String) {
        viewModelScope.launch {
            repository.updateTag(TagsEntity(tagId, text))
        }
    }

    fun readAllNotesByTags(tagId: Long) = repository.getNotesByTagId(tagId)

    fun readAllTags() = repository.readAllTags

    fun readAllNotesWithTags() = repository.readAllNotesWithTag

    fun getTagsByIds(ids: LongArray?) = repository.getTagsByIds(ids)

}
