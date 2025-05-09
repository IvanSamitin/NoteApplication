package com.example.noteapplication8.app

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.noteapplication8.di.appModule
import com.example.noteapplication8.model.KoinWorkerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(appModule)
        }

        // Регистрация WorkerFactory
        val workManagerFactory: WorkerFactory = KoinWorkerFactory()
        val configuration = Configuration.Builder()
            .setWorkerFactory(workManagerFactory)
            .build()
        WorkManager.initialize(this, configuration)
    }
    companion object {
        lateinit var appContext: Context
    }
}