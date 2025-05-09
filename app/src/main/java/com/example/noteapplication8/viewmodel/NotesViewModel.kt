package com.example.noteapplication8.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.model.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    val repository: NoteRepository // ✅ Сделан public
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean> get() = _authState

    // Слушатель изменений статуса аутентификации
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _authState.postValue(auth.currentUser != null)
    }

    init {
        // Инициализация слушателя при создании ViewModel
        firebaseAuth.addAuthStateListener(authStateListener)
        // Проверка текущего статуса при старте
        _authState.postValue(firebaseAuth.currentUser != null)
    }

    // Чтение данных
    fun readAllNotesByTags(tagId: String) = repository.getNotesByTagId(tagId)
    val readAllTags get() = repository.readAllTags
    val readAllNotesWithTag get() = repository.readAllNotesWithTag
    fun getTagsByIds(ids: Array<String>?) = repository.getTagsByIds(ids)


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

    fun signOut() {
        viewModelScope.launch {
            viewModelScope.launch(Dispatchers.IO) {
                repository.clearDatabase()
                Log.d("checkData", "Database cleared")

            }
        }
        repository.signOut()
    }

    // Синхронизация
    fun syncNotesFromFirebase(onSyncComplete: () -> Unit) {
        repository.syncNotesFromFirebase(onSyncComplete)
    }

    fun syncTagsFromFirebase(onSyncComplete: () -> Unit) {
        repository.syncTagsFromFirebase(onSyncComplete)
    }

    // Получение ID пользователя
    fun getCurrentUserUids(): String? = repository.getCurrentUserId()

    fun createNoteWithoutTag(date: String, header: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.createNoteWithoutTag(note)
            repository.triggerImmediateSync()
        }
    }

    fun forceSync() {
        repository.triggerImmediateSync()
        Log.d("checkData", "Force sync triggered")
    }

    // Создание заметки с тегами
    fun createNoteWithTags(date: String, header: String, text: String, tagIds: Array<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = NoteEntity(
                noteId = UUID.randomUUID().toString(),
                date = date,
                header = header,
                text = text,
                isSynced = false
            )
            repository.createNoteWithTags(note, tagIds)
            repository.triggerImmediateSync()
        }
    }

    // Обновление заметки без тегов
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
            repository.triggerImmediateSync()
        }
    }

    // Обновление заметки с тегами
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
            repository.triggerImmediateSync()
        }
    }

    // Создание тега
    fun createTag(text: String) {
        viewModelScope.launch {
            repository.createTag(TagsEntity(text = text, isSynced = false))
            repository.triggerImmediateSync()
        }
    }

    // Обновление тега
    fun updateTag(tagId: String, text: String) {
        viewModelScope.launch {
            repository.updateTag(TagsEntity(tagId, text, isSynced = false))
            repository.triggerImmediateSync()
        }
    }

    // Удаление заметки
    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotePermanently(noteId)
            repository.triggerImmediateSync()
        }
    }

    // Удаление тега
    fun deleteTag(tag: TagsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTagPermanently(tag.tagId)
            repository.triggerImmediateSync()
        }
    }

    // Синхронизация при входе
    fun login(onSuccess: () -> Unit) {
        repository.login(
            {
                viewModelScope.launch(Dispatchers.IO) {
//                    repository.clearDatabase()
//                    val userId = repository.getCurrentUserId() ?: return@launch
//                    repository.updateOrphanedNotes(userId) // Привязка данных
//                    repository.startSyncForUser()
//                    repository.triggerImmediateSync() // Немедленная синхронизация
                    syncNotesFromFirebase {

                    }

                    syncTagsFromFirebase {

                    }
                }
                onSuccess()
            },
            { error -> Log.e("Auth", "Ошибка: $error") }
        )
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun getCurrentUserEmail(): String? = repository.getCurrentUserEmail()

}