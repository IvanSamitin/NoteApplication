package com.example.noteapplication8.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.noteapplication8.model.entity.TagsEntity

@Dao
interface TagDao {
    // Базовые операции
    @Insert
    suspend fun insertTag(tag: TagsEntity)

    @Delete
    suspend fun deleteTag(tag: TagsEntity)

    @Update
    suspend fun updateTag(tag: TagsEntity)

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<TagsEntity>>

    // Проверка наличия заметок у тега
    @Query("SELECT COUNT(*) FROM note_with_tag WHERE tagId = :tagId")
    suspend fun getNoteCountForTag(tagId: String): Int // ✅ Используем String

    // Удаление тега с каскадным удалением заметок
    @Transaction
    suspend fun deleteTagWithNotes(tagId: String) { // ✅ Используем String
        deleteTagRelations(tagId)
        deleteTagById(tagId)
    }

    @Query("DELETE FROM note_with_tag WHERE tagId = :tagId")
    suspend fun removeAllTags(tagId: String) // ✅ Используем String

    @Query("DELETE FROM note_with_tag WHERE tagId = :tagId")
    suspend fun deleteTagRelations(tagId: String) // ✅ Используем String

    @Query("DELETE FROM tags WHERE tagId = :tagId")
    suspend fun deleteTagById(tagId: String) // ✅ Используем String

    @Query("SELECT * FROM tags WHERE tagId IN (:ids)")
    fun getTagsByIds(ids: Array<String>?): LiveData<List<TagsEntity>> // ✅ Используем Array<String>

    // Синхронизация
    @Query("SELECT * FROM tags WHERE tagId = :tagId")
    suspend fun getTagById(tagId: String): TagsEntity?

    @Query("SELECT * FROM tags WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedTags(userId: String): List<TagsEntity>
}
