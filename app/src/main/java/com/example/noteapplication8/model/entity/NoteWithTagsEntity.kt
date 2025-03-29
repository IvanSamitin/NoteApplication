package com.example.noteapplication8.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "note_with_tag",
    primaryKeys = ["noteId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["noteId"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagsEntity::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteWithTagsEntity(
    val noteId: Long,
    val tagId: Long
)
