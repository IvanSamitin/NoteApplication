package com.example.noteapplication8.model.repository

import androidx.lifecycle.LiveData
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.NoteWithTagsDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.NoteWithTagsEntity
import com.example.noteapplication8.model.entity.TagsEntity

class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val relationDao: NoteWithTagsDao
) {

    suspend fun createNoteWithoutTag(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun createTag(tag: TagsEntity) = tagDao.insertTag(tag)

    suspend fun deleteNoteWithoutTag(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteTag(tag: TagsEntity) = tagDao.deleteTag(tag)

    val readAllNotes: LiveData<List<NoteEntity>>
        get() = noteDao.getAllNotes()

    val readAllTags: LiveData<List<TagsEntity>>
        get() = tagDao.getAllTags()

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun updateTag(tag: TagsEntity) = tagDao.updateTag(tag)

//    suspend fun getAllNotesWithTags() = relationDao.getAllNotesWithTags()
//
//    // Создать заметку с существующим тегом
//    suspend fun createNoteWithExistingTag(note: NoteEntity, tagId: Long): Long {
//        val noteId = noteDao.insertNote(note)
//        relationDao.addTagToNote(NoteWithTagsEntity(noteId, tagId))
//        return noteId
//    }
//
//    // Создать заметку с новым тегом
//    suspend fun createNoteWithNewTag(note: NoteEntity, tagText: String): Long {
//        val tagId = tagDao.insertTag(TagsEntity(text = tagText))
//        return createNoteWithExistingTag(note, tagId)
//    }
//
//    // Удалить заметку (без удаления тегов)
//    suspend fun deleteNoteKeepingTags(note: NoteEntity) {
//        noteDao.deleteNote(note)
//        relationDao.removeAllTagsFromNote(note.noteId)
//    }
//
//    // Удалить заметку и связанные теги (если они больше не используются)
//    suspend fun deleteNoteWithOrphanedTags(note: NoteEntity) {
//        val relations = relationDao.getTagsForNote(note.noteId)
//        noteDao.deleteNote(note)
//
//        relations.forEach { relation ->
//            if(tagDao.getNoteCountForTag(relation.tagId) == 0) {
//                tagDao.deleteTagById(relation.tagId)
//            }
//        }
//    }
//
//
//
//    // Удалить тег со всеми связанными заметками
//    suspend fun deleteTagWithAllNotes(tagId: Long) {
//        tagDao.deleteTagWithNotes(tagId)
//    }
}