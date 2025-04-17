package com.example.noteapplication8.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val date: String,
    val header: String,
    val text: String,
) : Parcelable
