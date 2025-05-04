package com.example.noteapplication8.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val noteId: String = UUID.randomUUID().toString(), // Изменено на String
    val userId: String? = null,
    val date: String,
    val header: String,
    val text: String,
    val isSynced: Boolean = false
) : Parcelable
