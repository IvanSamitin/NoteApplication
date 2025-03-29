package com.example.noteapplication8.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication8.model.datasource.NoteDatabase
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.model.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesViewModel(application: Application) : AndroidViewModel(application)  {
    val context = application

    val noteDao = NoteDatabase.getDatabase(context = context).noteDao()
    val noteWithTagsDao = NoteDatabase.getDatabase(context = context).noteWithTagsDao()
    val tagDao = NoteDatabase.getDatabase(context = context).tagDao()
    var repository = NoteRepository(noteDao, tagDao, noteWithTagsDao)

    fun createNoteWithoutTag(note: NoteEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createNoteWithoutTag(note = note)
        }
    }

    fun createTag(tag: TagsEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createTag(tag = tag)
        }
    }

    fun updateNote(note: NoteEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note = note)
        }
    }

    fun updateTag(tag: TagsEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTag(tag = tag)
        }
    }

    fun deleteNote(note: NoteEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNoteWithoutTag(note = note)
        }
    }

    fun deleteTag(tag: TagsEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTag(tag = tag)
        }
    }

    fun readAllNotesByTags(tagId: Long) = repository.getNotesByTagId(tagId)

    fun readAllTags() = repository.readAllTags

    fun readAllNotesWithTags() = repository.readAllNotesWithTag

    fun createNoteWithTags(note: NoteEntity, tagIds: LongArray){
        viewModelScope.launch(Dispatchers.IO){
            repository.createNoteWithTags(note, tagIds)
        }
    }

    fun updateNoteWithTags(note: NoteEntity, tagIds: LongArray){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateNoteWithTags(note, tagIds)
        }
    }

    fun getTagsByIds(ids: LongArray?) = repository.getTagsByIds(ids)

}


