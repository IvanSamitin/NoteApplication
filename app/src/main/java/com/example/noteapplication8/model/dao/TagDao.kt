package com.example.noteapplication8.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagWithNotes
import com.example.noteapplication8.model.entity.TagsEntity

@Dao
interface TagDao {
    // Базовые операции
    @Insert
    suspend fun insertTag(tag: TagsEntity): Long

    @Delete
    suspend fun deleteTag(tag: TagsEntity)

    @Update
    suspend fun updateTag(tag: TagsEntity)

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<TagsEntity>>


    // Проверка наличия заметок у тега
    @Query("SELECT COUNT(*) FROM note_with_tag WHERE tagId = :tagId")
    suspend fun getNoteCountForTag(tagId: Long): Int

    // Удаление тега с каскадным удалением заметок (если нужно)
    @Transaction
    suspend fun deleteTagWithNotes(tagId: Long) {
        // Удаляем связи
        deleteTagRelations(tagId)
        // Удаляем сам тег
        deleteTagById(tagId)
    }

    @Query("DELETE FROM note_with_tag WHERE tagId = :tagId")
    suspend fun removeAllTags(tagId: Long)

    @Query("DELETE FROM note_with_tag WHERE tagId = :tagId")
    suspend fun deleteTagRelations(tagId: Long)

    @Query("DELETE FROM tags WHERE tagId = :tagId")
    suspend fun deleteTagById(tagId: Long)
}