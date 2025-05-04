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

    @Insert
    suspend fun insertNoteWithTag(noteWithTag: NoteWithTagsEntity)

    // Транзакция для создания заметки с тегами
    @Transaction
    suspend fun createNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>,
    ) {
        val noteId = insertNote(note)
        tagIds.forEach { tagId ->
            insertNoteWithTag(
                NoteWithTagsEntity(
                    noteId = noteId.toString(), // ✅ Long -> String
                    tagId = tagId.toString(),   // ✅ Long -> String
                ),
            )
        }
    }

    @Update
    suspend fun updateNote(note: NoteEntity)

    // Удаление всех тегов у заметки
    @Query("DELETE FROM note_with_tag WHERE noteId = :noteId")
    suspend fun deleteNoteTags(noteId: String) // ✅ Используем String

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
