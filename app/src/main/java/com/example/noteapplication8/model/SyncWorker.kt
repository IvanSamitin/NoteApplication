package com.example.noteapplication8.model

import android.content.Context
import androidx.work.*
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.noteapplication8.model.dao.NoteDao
import com.example.noteapplication8.model.dao.TagDao
import com.example.noteapplication8.model.datasource.FirebaseService
import com.example.noteapplication8.model.repository.NoteRepository
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val firebaseService: FirebaseService,
    private val noteRepository: NoteRepository
) : CoroutineWorker(context, params), KoinComponent {

    private val userId by lazy { params.inputData.getString("USER_ID") ?: "" }

    override suspend fun doWork(): Result = coroutineScope {
        if (userId.isEmpty()) return@coroutineScope Result.failure()

        // Синхронизация заметок
        noteRepository.uploadUnsyncedNotes(userId)

        // Синхронизация тегов
        noteRepository.uploadUnsyncedTags(userId)

        Result.success()
    }

    companion object {
        fun start(context: Context, userId: String) {
            val data = Data.Builder().putString("USER_ID", userId).build()
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
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
    }
}