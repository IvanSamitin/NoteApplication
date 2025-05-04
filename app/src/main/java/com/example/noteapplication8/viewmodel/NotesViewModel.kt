package com.example.noteapplication8.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.model.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    val repository: NoteRepository // ✅ Сделан public
) : ViewModel() {

    // Методы для работы с заметками
    fun createNoteWithoutTag(date: String, header: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.createNoteWithoutTag(note)
            repository.startSyncForUser()
        }
    }

    fun createNoteWithTags(date: String, header: String, text: String, tagIds: Array<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                noteId = UUID.randomUUID().toString(), // ✅ UUID — это String
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.createNoteWithTags(note, tagIds) // ✅ Если нужно для Room
        }
    }

    fun updateNoteWithoutTags(noteId: String, date: String, header: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                noteId = noteId,
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.updateNote(note)
            repository.startSyncForUser()
        }
    }

    fun updateNoteWithTags(
        noteId: String,
        date: String,
        header: String,
        text: String,
        tagIds: Array<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                noteId = noteId,
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.updateNoteWithTags(note, tagIds)
            repository.startSyncForUser()
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNoteWithoutTag(noteId)
            repository.startSyncForUser()
        }
    }

    fun deleteTag(tag: TagsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTag(tag.copy(isSynced = false))
            repository.startSyncForUser()
        }
    }

    fun createTag(text: String) {
        viewModelScope.launch {
            repository.createTag(TagsEntity(text = text, isSynced = false))
            repository.startSyncForUser()
        }
    }

    fun updateTag(tagId: String, text: String) {
        viewModelScope.launch {
            repository.updateTag(TagsEntity(tagId, text, isSynced = false))
            repository.startSyncForUser()
        }
    }

    // Чтение данных
    fun readAllNotesByTags(tagId: String) = repository.getNotesByTagId(tagId)
    val readAllTags get() = repository.readAllTags
    val readAllNotesWithTag get() = repository.readAllNotesWithTag
    fun getTagsByIds(ids: Array<String>?) = repository.getTagsByIds(ids)

    // Авторизация
    fun login(onSuccess: () -> Unit) {
        repository.login(
            {
                repository.startSyncForUser()
                onSuccess()
            },
            { Log.d("checkData", it) }
        )
    }

    fun register(onSuccess: () -> Unit) {
        repository.register(
            {
                repository.startSyncForUser()
                onSuccess()
            },
            { Log.d("checkData", it) }
        )
    }

    fun isUserAuthenticated() = repository.isUserAuthenticated()
    fun signOut() = repository.signOut()

    // Синхронизация
    fun syncNotesFromFirebase(onSyncComplete: () -> Unit) {
        repository.syncNotesFromFirebase(onSyncComplete)
    }

    fun syncTagsFromFirebase(onSyncComplete: () -> Unit) {
        repository.syncTagsFromFirebase(onSyncComplete)
    }

    // Получение ID пользователя
    fun getCurrentUserUids(): String? = repository.getCurrentUserId()
}