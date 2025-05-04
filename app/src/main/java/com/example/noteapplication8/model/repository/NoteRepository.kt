package com.example.noteapplication8.model.repository

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
import com.example.noteapplication8.model.entity.TagsEntity
import com.example.noteapplication8.utils.Constants.LOGIN
import com.example.noteapplication8.utils.Constants.PASSWORD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val relationDao: NoteWithTagsDao,
    private val firebaseService: FirebaseService
) {
    private val mAuth = FirebaseAuth.getInstance()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Запуск синхронизации при входе пользователя
    fun startSyncForUser() {
        val userId = getCurrentUserId() ?: return
        SyncWorker.start(App.appContext, userId)
    }

    // Синхронизация заметок из Firebase в локальную БД
    fun syncNotesFromFirebase(onSyncComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        firebaseService.observeNotes(userId) { remoteNotes ->
            repositoryScope.launch {
                updateLocalNotes(remoteNotes)
                onSyncComplete()
            }
        }
    }

    // Синхронизация тегов из Firebase в локальную БД
    fun syncTagsFromFirebase(onSyncComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        firebaseService.observeTags(userId) { remoteTags ->
            repositoryScope.launch {
                updateLocalTags(remoteTags)
                onSyncComplete()
            }
        }
    }

    // Обновление локальных заметок из Firebase
    private suspend fun updateLocalNotes(remoteNotes: List<FirebaseNote>) {
        for (firebaseNote in remoteNotes) {
            val localNote = noteDao.getNoteById(firebaseNote.noteId)
            if (localNote == null) {
                noteDao.insertNote(NoteEntity(
                    noteId = firebaseNote.noteId,
                    userId = firebaseNote.userId,
                    date = firebaseNote.date,
                    header = firebaseNote.header,
                    text = firebaseNote.text,
                    isSynced = true
                ))
            } else if (!localNote.isSynced) {
                noteDao.updateNote(localNote.copy(isSynced = true))
            }
        }
    }

    // Обновление локальных тегов из Firebase
    private suspend fun updateLocalTags(remoteTags: List<FirebaseTag>) {
        for (firebaseTag in remoteTags) {
            val localTag = tagDao.getTagById(firebaseTag.tagId)
            if (localTag == null) {
                tagDao.insertTag(TagsEntity(
                    tagId = firebaseTag.tagId,
                    userId = firebaseTag.userId,
                    text = firebaseTag.text,
                    isSynced = true
                ))
            } else if (!localTag.isSynced) {
                tagDao.updateTag(localTag.copy(isSynced = true))
            }
        }
    }

    // Отправка локальных заметок в Firebase
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

    // Отправка локальных тегов в Firebase
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

    // Чтение данных
    val readAllTags: LiveData<List<TagsEntity>>
        get() = tagDao.getAllTags()

    val readAllNotesWithTag: LiveData<List<NoteWithTags>>
        get() = relationDao.getAllNotesWithTags()

    suspend fun createNoteWithTags(
        note: NoteEntity,
        tagIds: Array<String>,
    ) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId)
        relationDao.createNoteWithTags(noteWithUser, tagIds)
    }

    suspend fun updateNoteWithTags(note: NoteEntity, tagIds: Array<String>) {
        val userId = getCurrentUserId()
        val noteWithUser = note.copy(userId = userId)
        relationDao.updateNoteWithTags(noteWithUser, tagIds)
    }

    suspend fun createNoteWithoutTag(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun createTag(tag: TagsEntity) = tagDao.insertTag(tag)

    // ✅ Исправлено: noteId теперь String
    suspend fun deleteNoteWithoutTag(noteId: String) = noteDao.deleteNoteById(noteId)

    suspend fun deleteTag(tag: TagsEntity) = tagDao.deleteTag(tag)

    // ✅ Исправлено: tagId теперь String
    fun getNotesByTagId(tagId: String) = relationDao.getNotesByTagId(tagId)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun updateTag(tag: TagsEntity) = tagDao.updateTag(tag)

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

    fun getCurrentUserId(): String? = mAuth.currentUser?.uid
}
