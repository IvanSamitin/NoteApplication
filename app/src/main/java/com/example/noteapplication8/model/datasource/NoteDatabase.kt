package com.example.noteapplication8.model.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.NoteWithTagsDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTagsEntity
import com.example.noteapplication8.model.entity.TagsEntity


@Database(entities = [NoteEntity::class, TagsEntity::class, NoteWithTagsEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    abstract fun noteWithTagsDao(): NoteWithTagsDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {

            return if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE as NoteDatabase
            } else INSTANCE as NoteDatabase
        }
    }
}