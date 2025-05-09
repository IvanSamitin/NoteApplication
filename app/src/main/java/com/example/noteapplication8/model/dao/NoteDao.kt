package com.example.noteapplication8.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.TagsEntity

@Dao
interface NoteDao {
    // Базовые операции
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Insert
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNoteById(noteId: String) // ✅ Используем String

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Query("UPDATE notes SET isDeleted = 1 WHERE noteId = :noteId")
    suspend fun markNoteAsDeleted(noteId: String)

    @Query("SELECT * FROM notes WHERE isDeleted = 1 AND userId = :userId")
    suspend fun getDeletedNotes(userId: String): List<NoteEntity>

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNotePermanently(noteId: String)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET userId = :userId WHERE userId IS NULL OR userId = ''")
    suspend fun updateOrphanedNotes(userId: String)

    // Получение заметок с тегами
    @Transaction
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesWithTags(): List<NoteWithTags>

    // Поиск по тегам
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
    suspend fun getNotesByTag(tagId: String): List<NoteWithTags> // ✅ Используем String

    // Синхронизация
    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedNotes(userId: String): List<NoteEntity>

    @Query("SELECT tagId FROM note_with_tag WHERE noteId = :noteId")
    suspend fun getNoteTagIds(noteId: String): List<String>

    @Query("SELECT userId FROM notes LIMIT 1")
    suspend fun getCurrentUserUid(): String?
}

