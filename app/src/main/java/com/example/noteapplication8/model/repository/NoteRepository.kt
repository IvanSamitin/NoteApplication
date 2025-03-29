package com.example.noteapplication8.model.repository

import androidx.lifecycle.LiveData
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.NoteWithTagsDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.TagsEntity

class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val relationDao: NoteWithTagsDao
) {
    val readAllTags: LiveData<List<TagsEntity>>
        get() = tagDao.getAllTags()

    val readAllNotesWithTag: LiveData<List<NoteWithTags>>
        get() = relationDao.getAllNotesWithTags()



    suspend fun createNoteWithTags(note: NoteEntity, tagIds: LongArray) = relationDao.createNoteWithTags(note, tagIds)

    suspend fun updateNoteWithTags(note: NoteEntity, tagIds: LongArray) = relationDao.updateNoteWithTags(note, tagIds)

    suspend fun createNoteWithoutTag(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun createTag(tag: TagsEntity) = tagDao.insertTag(tag)

    suspend fun deleteNoteWithoutTag(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteTag(tag: TagsEntity) = tagDao.deleteTag(tag)

    fun getNotesByTagId(tagId: Long) = relationDao.getNotesByTagId(tagId)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun updateTag(tag: TagsEntity) = tagDao.updateTag(tag)

    fun getTagsByIds(ids: LongArray?) = tagDao.getTagsByIds(ids)
}