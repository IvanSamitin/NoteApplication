package com.example.noteapplication8.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "tags")
data class TagsEntity(
    @PrimaryKey val tagId: String = UUID.randomUUID().toString(),
    val text: String,
    val userId: String? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false // Новый флаг
) : Parcelable