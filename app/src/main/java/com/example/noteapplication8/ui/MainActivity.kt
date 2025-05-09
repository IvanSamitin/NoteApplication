package com.example.noteapplication8.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.noteapplication8.R
import com.example.noteapplication8.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<NotesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        setupObservers()
    }

    private fun setupObservers() {
        // Отслеживаем изменение статуса авторизации
        viewModel.getCurrentUserUids()?.let { userId ->
            // Запуск синхронизации при входе
            viewModel.repository.startSyncForUser()

            // Синхронизация заметок при входе
            viewModel.syncNotesFromFirebase {
//                viewModel.readAllNotesWithTag.observe(this, Observer {
//
//                })
            }

            viewModel.syncTagsFromFirebase {
//                viewModel.readAllTags.observe(this, Observer {
//
//                })
            }


        }
    }
}