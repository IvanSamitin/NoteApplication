package com.example.noteapplication8.model.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteWithTags(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "tagId",
        associateBy = Junction(NoteWithTagsEntity::class),
    )
    val tags: List<TagsEntity>,
) : Parcelable
