package com.example.noteapplication8.model

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.noteapplication8.model.repository.NoteRepository
import org.koin.core.component.KoinComponent

class KoinWorkerFactory : WorkerFactory(), KoinComponent {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return try {
            // Для CoroutineWorker требуется только context и params
            val workerClass = Class.forName(workerClassName)
                .asSubclass(CoroutineWorker::class.java)

            workerClass.getDeclaredConstructor(
                Context::class.java,
                WorkerParameters::class.java
            ).newInstance(appContext, workerParameters)
        } catch (e: Exception) {
            Log.e("KoinWorkerFactory", "Error creating $workerClassName", e)
            null
        }
    }
}