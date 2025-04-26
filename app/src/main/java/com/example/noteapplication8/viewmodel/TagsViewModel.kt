package com.example.noteapplication8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.model.repository.NoteRepository
import kotlinx.coroutines.launch

class TagsViewModel(private val repository: NoteRepository) : ViewModel() {

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

    fun deleteTag(tag: TagsEntity) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }

    fun getAllTags() = repository.readAllTags
    fun getTagsByIds(ids: LongArray?) = repository.getTagsByIds(ids)
}