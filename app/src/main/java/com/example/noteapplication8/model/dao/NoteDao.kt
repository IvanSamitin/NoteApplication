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

@Dao
interface NoteDao {
    // Базовые операции
    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Update
    suspend fun updateNote(note: NoteEntity)

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
    suspend fun getNotesByTag(tagId: Long): List<NoteWithTags>
}