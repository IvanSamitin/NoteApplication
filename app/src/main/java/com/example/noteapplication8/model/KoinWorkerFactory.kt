package com.example.noteapplication8.model

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class KoinWorkerFactory : WorkerFactory(), KoinComponent {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return try {
            val clazz = Class.forName(workerClassName).kotlin
            if (CoroutineWorker::class.java.isAssignableFrom(clazz.java)) {
                val worker = clazz.objectInstance as? CoroutineWorker
                    ?: clazz.constructors.first().call(appContext, workerParameters)
                worker as? ListenableWorker
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}