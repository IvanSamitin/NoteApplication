package com.example.noteapplication8.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.NoteWithTagsEntity

@Dao
interface NoteWithTagsDao {

    // Вставка заметки
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTags(): LiveData<List<NoteWithTags>>

    @Transaction
    @Query("""
        SELECT * FROM notes 
        WHERE noteId IN (
            SELECT noteId FROM note_with_tag 
            WHERE tagId = :tagId
        )
    """)
    fun getNotesByTagId(tagId: Long): LiveData<List<NoteWithTags>>

    @Insert
    suspend fun insertNoteWithTag(noteWithTag: NoteWithTagsEntity)



    // Транзакция для создания заметки с тегами
    @Transaction
    suspend fun createNoteWithTags(note: NoteEntity, tagIds: LongArray) {
        // 1. Вставляем заметку и получаем её ID
        val noteId = insertNote(note)

        // 2. Вставляем связи с тегами
        tagIds.forEach { tagId ->
            insertNoteWithTag(
                NoteWithTagsEntity(
                    noteId = noteId,
                    tagId = tagId
                )
            )
        }
    }

    @Update
    suspend fun updateNote(note: NoteEntity)

    // Удаление всех тегов у заметки
    @Query("DELETE FROM note_with_tag WHERE noteId = :noteId")
    suspend fun deleteNoteTags(noteId: Long)

    // Полная транзакция для обновления заметки с тегами
    @Transaction
    suspend fun updateNoteWithTags(note: NoteEntity, tagIds: LongArray) {
        // 1. Обновляем саму заметку
        updateNote(note)

        // 2. Удаляем все существующие связи с тегами
        deleteNoteTags(note.noteId)

        // 3. Добавляем новые связи с тегами
        tagIds.forEach { tagId ->
            insertNoteWithTag(
                NoteWithTagsEntity(
                    noteId = note.noteId,
                    tagId = tagId
                )
            )
        }
    }

//
//    @Delete
//    suspend fun removeTagFromNote(relation: NoteWithTagsEntity)
//
//    @Query("DELETE FROM note_with_tag WHERE noteId = :noteId")
//    suspend fun removeAllTagsFromNote(noteId: Long)
//
//    @Query("SELECT * FROM note_with_tag WHERE noteId = :noteId")
//    suspend fun getTagsForNote(noteId: Long): List<NoteWithTagsEntity>
}