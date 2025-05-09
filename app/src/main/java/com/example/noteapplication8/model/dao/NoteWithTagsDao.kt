package com.example.noteapplication8.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
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
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM note_with_tag")
    suspend fun deleteAllNotesTags()

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTags(): LiveData<List<NoteWithTags>>

    @Transaction
    @Query(
        """
        SELECT * FROM notes 
        WHERE noteId IN (
            SELECT noteId FROM note_with_tag 
            WHERE tagId = :tagId
        )
    """
    )
    fun getNotesByTagId(tagId: String): LiveData<List<NoteWithTags>> // ✅ Используем String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteWithTag(noteWithTag: NoteWithTagsEntity)

    @Query("DELETE FROM note_with_tag WHERE noteId = :noteId")
    suspend fun deleteNoteTags(noteId: String)

    @Transaction
    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    suspend fun getNoteWithTagsById(noteId: String): NoteWithTags?


    // Транзакция для создания заметки с тегами
    @Transaction
    suspend fun createNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>,
    ) {
        // 1. Сначала сохраняем заметку
        insertNote(note)

        // 2. Теперь создаем связи
        for (tagId in tagIds) {
            insertNoteWithTag(NoteWithTagsEntity(note.noteId, tagId))
        }
    }

    @Update
    suspend fun updateNote(note: NoteEntity)

    // Полная транзакция для обновления заметки с тегами
    @Transaction
    suspend fun updateNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>,
    ) {
        updateNote(note)
        deleteNoteTags(note.noteId)
        tagIds.forEach { tagId ->
            insertNoteWithTag(
                NoteWithTagsEntity(
                    noteId = note.noteId.toString(),
                    tagId = tagId.toString(), // ✅ Long -> String
                ),
            )
        }
    }
}
