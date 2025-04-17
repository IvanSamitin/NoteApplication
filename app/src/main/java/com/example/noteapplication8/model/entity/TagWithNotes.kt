package com.example.noteapplication8.model.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithNotes(
    @Embedded val tag: TagsEntity,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "noteId",
        associateBy = Junction(NoteWithTagsEntity::class),
    )
    val notes: List<NoteEntity>,
)
