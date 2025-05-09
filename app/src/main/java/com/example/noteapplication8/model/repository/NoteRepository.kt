package com.example.noteapplication8.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.noteapplication8.app.App
import com.example.noteapplication8.model.SyncWorker
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.NoteWithTagsDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.datasource.FirebaseService
import com.example.noteapplication8.model.entity.FirebaseNote
import com.example.noteapplication8.model.entity.FirebaseTag
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.NoteWithTags
import com.example.noteapplication8.model.entity.NoteWithTagsEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.utils.Constants.LOGIN
import com.example.noteapplication8.utils.Constants.PASSWORD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val relationDao: NoteWithTagsDao,
    private val firebaseService: FirebaseService
) {
    private val mAuth = FirebaseAuth.getInstance()



    suspend fun clearDatabase() {
        noteDao.deleteAllNotes()
        tagDao.deleteAllTags()
        relationDao.deleteAllNotesTags()
        // Добавьте очистку других таблиц при необходимости

    }


    fun triggerImmediateSync() {
        repositoryScope.launch {
            val userId = getCurrentUserId() ?: return@launch
            SyncWorker.startImmediate(App.appContext, userId)
        }
    }

    // Запуск периодической синхронизации
    fun startSyncForUser() {
        val userId = getCurrentUserId() ?: return
        SyncWorker.startPeriodic(App.appContext, userId)
    }

    suspend fun updateOrphanedNotes(userId: String) = noteDao.updateOrphanedNotes(userId)

    // Синхронизация заметок
    fun syncNotesFromFirebase(onSyncComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        firebaseService.observeNotes(userId) { remoteNotes ->
            repositoryScope.launch {
                updateLocalNotes(remoteNotes)
                onSyncComplete()
            }
        }
    }

    // Синхронизация тегов
    fun syncTagsFromFirebase(onSyncComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        firebaseService.observeTags(userId) { remoteTags ->
            repositoryScope.launch {
                updateLocalTags(remoteTags)
                onSyncComplete()
            }
        }
    }


    private suspend fun updateLocalNotes(remoteNotes: List<FirebaseNote>) {
        Log.d("Sync", "Синхронизация заметок из Firebase. Количество: ${remoteNotes.size}")
        for (firebaseNote in remoteNotes) {
            Log.d("Sync", "Обработка заметки: ${firebaseNote.noteId}, теги: ${firebaseNote.tagIds}")

            val localNote = noteDao.getNoteById(firebaseNote.noteId)

            if (localNote == null) {
                val newNote = NoteEntity(
                    noteId = firebaseNote.noteId,
                    userId = firebaseNote.userId,
                    date = firebaseNote.date,
                    header = firebaseNote.header,
                    text = firebaseNote.text,
                    isSynced = true
                )
                noteDao.insertNote(newNote)
                Log.d("Sync", "Создана новая заметка: ${newNote.noteId}")

                for (tagId in firebaseNote.tagIds) {
                    relationDao.insertNoteWithTag(NoteWithTagsEntity(newNote.noteId, tagId))
                    Log.d("Sync", "Добавлен тег $tagId для заметки ${newNote.noteId}")
                }
            } else if (!localNote.isSynced) {
                noteDao.updateNote(localNote.copy(isSynced = true))
                Log.d("Sync", "Обновлена заметка: ${localNote.noteId}")

                relationDao.deleteNoteTags(localNote.noteId)
                for (tagId in firebaseNote.tagIds) {
                    relationDao.insertNoteWithTag(NoteWithTagsEntity(localNote.noteId, tagId))
                    Log.d("Sync", "Добавлен тег $tagId для заметки ${localNote.noteId}")
                }
            }
        }
    }

    // Обновление локальных тегов из Firebase
    private suspend fun updateLocalTags(remoteTags: List<FirebaseTag>) {
        for (firebaseTag in remoteTags) {
            val localTag = tagDao.getTagById(firebaseTag.tagId)
            if (localTag == null) {
                tagDao.insertTag(
                    TagsEntity(
                        tagId = firebaseTag.tagId,
                        userId = firebaseTag.userId,
                        text = firebaseTag.text,
                        isSynced = true
                    )
                )
            } else if (!localTag.isSynced) {
                tagDao.updateTag(localTag.copy(isSynced = true))
            }
        }
    }

    // Отправка локальных заметок в Firebase


    // Чтение данных
    val readAllTags: LiveData<List<TagsEntity>>
        get() = tagDao.getAllTags()

    val readAllNotesWithTag: LiveData<List<NoteWithTags>>
        get() = relationDao.getAllNotesWithTags()


    suspend fun createNoteWithoutTag(note: NoteEntity) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId, isSynced = false)
        relationDao.insertNote(noteWithUser)
        uploadUnsyncedNotes(userId ?: "")
        triggerImmediateSync()
    }


    // ✅ Исправлено: noteId теперь String
    suspend fun deleteNoteWithoutTag(noteId: String) = noteDao.deleteNoteById(noteId)

    suspend fun deleteTag(tag: TagsEntity) = tagDao.deleteTag(tag)

    // ✅ Исправлено: tagId теперь String
    fun getNotesByTagId(tagId: String) = relationDao.getNotesByTagId(tagId)

    suspend fun updateNote(note: NoteEntity) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId, isSynced = false)
        relationDao.updateNote(noteWithUser)
        uploadUnsyncedNotes(userId ?: "")
        triggerImmediateSync()

    }


    // ✅ Исправлено: ids теперь Array<String>
    fun getTagsByIds(ids: Array<String>?) = tagDao.getTagsByIds(ids)

    fun signOut() {
        mAuth.signOut()
    }

    fun login(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        mAuth.signInWithEmailAndPassword(LOGIN, PASSWORD)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { onFail(it.message.toString()) }
    }

    fun register(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        mAuth.createUserWithEmailAndPassword(LOGIN, PASSWORD)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message.toString()) }
    }


    fun isUserAuthenticated() = mAuth.currentUser != null


    private val repositoryScope = CoroutineScope(Dispatchers.IO)


    // Синхронизация заметок
    suspend fun uploadUnsyncedNotes(userId: String) {
        val unsyncedNotes = noteDao.getUnsyncedNotes(userId)
        for (note in unsyncedNotes) {
            val tagIds = noteDao.getNoteTagIds(note.noteId)
            try {
                firebaseService.uploadNote(note, tagIds)
                noteDao.updateNote(note.copy(isSynced = true))
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    // Синхронизация тегов
    suspend fun uploadUnsyncedTags(userId: String) {
        val unsyncedTags = tagDao.getUnsyncedTags(userId)
        for (tag in unsyncedTags) {
            try {
                firebaseService.uploadTag(tag)
                tagDao.updateTag(tag.copy(isSynced = true))
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    // Создание заметки с тегами
    suspend fun createNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>,
    ) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId, isSynced = false)
        relationDao.createNoteWithTags(noteWithUser, tagIds)
        uploadUnsyncedNotes(userId ?: "")
        triggerImmediateSync()
    }

    // Обновление заметки с тегами
    suspend fun updateNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>
    ) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId, isSynced = false)
        relationDao.updateNoteWithTags(noteWithUser, tagIds)
        uploadUnsyncedNotes(userId ?: "")
        triggerImmediateSync()
    }

    // Создание тега
    suspend fun createTag(tag: TagsEntity) {
        val userId = getCurrentUserId()
        val tagWithUser = tag.copy(userId = userId, isSynced = false)
        tagDao.insertTag(tagWithUser)
        uploadUnsyncedTags(userId ?: "")
        triggerImmediateSync()
    }

    // Обновление тега
    suspend fun updateTag(tag: TagsEntity) {
        val userId = getCurrentUserId()
        val tagWithUser = tag.copy(userId = userId, isSynced = false)
        tagDao.updateTag(tagWithUser)
        uploadUnsyncedTags(userId ?: "")
        triggerImmediateSync()
    }

    // Удаление заметки
    fun deleteNotePermanently(noteId: String) {
        repositoryScope.launch {
            noteDao.markNoteAsDeleted(noteId)
            triggerImmediateSync()
        }
    }

    // Удаление тега
    fun deleteTagPermanently(tagId: String) {
        repositoryScope.launch {
            tagDao.markTagAsDeleted(tagId)
            triggerImmediateSync()
        }
    }

    // Получение ID текущего пользователя
    fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    fun getCurrentUserEmail(): String? = mAuth.currentUser?.email
}
