package com.example.noteapplication8.di

import androidx.room.Room
import com.example.noteapplication8.model.datasource.NoteDatabase
import com.example.noteapplication8.model.repository.NoteRepository
import com.example.noteapplication8.viewmodel.NotesViewModel
import com.example.noteapplication8.viewmodel.TagsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            NoteDatabase::class.java,
            "note_database"
        ).fallbackToDestructiveMigration().build()
    }

    viewModel {
        NotesViewModel(
            repository = get()
        )
    }

    viewModel {
        TagsViewModel(
            repository = get()
        )
    }

    single { get<NoteDatabase>().noteDao() }
    single { get<NoteDatabase>().noteWithTagsDao() }
    single { get<NoteDatabase>().tagDao() }

    single {
        NoteRepository(
            noteDao = get(),
            tagDao = get(),
            relationDao = get()
        )
    }
}