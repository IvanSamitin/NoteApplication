package com.example.noteapplication8.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tags")
data class TagsEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val text: String
): Parcelable
