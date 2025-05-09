package com.example.noteapplication8.model

import android.content.Context
import android.util.Log
import androidx.work.*
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.datasource.FirebaseService
import com.example.noteapplication8.model.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit


// SyncWorker.kt
// SyncWorker.kt
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val noteDao: NoteDao by inject()
    private val tagDao: TagDao by inject()
    private val firebaseService: FirebaseService by inject()
    private val mAuth = FirebaseAuth.getInstance()

    override suspend fun doWork(): Result = coroutineScope {
        val userId = mAuth.currentUser!!.uid ?: return@coroutineScope Result.failure()

        // Обработка удалений
        processDeletedNotes(userId)
        processDeletedTags(userId)

        // Синхронизация заметок
        syncNotes(userId)
        syncTags(userId)

        Result.success()
    }

    private suspend fun processDeletedNotes(userId: String) {
        val deletedNotes = noteDao.getDeletedNotes(userId)
        for (note in deletedNotes) {
            firebaseService.deleteNote(note.noteId)
            noteDao.deleteNotePermanently(note.noteId)
        }
    }

    private suspend fun processDeletedTags(userId: String) {
        val deletedTags = tagDao.getDeletedTags(userId)
        for (tag in deletedTags) {
            firebaseService.deleteTag(tag.tagId)
            tagDao.deleteTagPermanently(tag.tagId)
        }
    }

    private suspend fun syncNotes(userId: String) {
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

    private suspend fun syncTags(userId: String) {
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

    companion object {
        // Периодическая синхронизация (2 минуты)
        fun startPeriodic(context: Context, userId: String) {
            val data = workDataOf("USER_ID" to userId)
            val request = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.SECONDS)
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "SyncWorker_$userId",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }

        // Немедленная синхронизация
        fun startImmediate(context: Context, userId: String) {
            val data = workDataOf("USER_ID" to userId)
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}