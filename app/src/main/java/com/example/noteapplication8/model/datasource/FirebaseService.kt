package com.example.noteapplication8.model.datasource

import com.example.noteapplication8.model.entity.FirebaseNote
import com.example.noteapplication8.model.entity.FirebaseTag
import com.example.noteapplication8.model.entity.NoteEntity
import com.example.noteapplication8.model.entity.TagsEntity
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesRef get() = firestore.collection("notes")
    private val tagsRef get() = firestore.collection("tags")

    // Suspend-функция для загрузки заметок
    suspend fun uploadNote(note: NoteEntity, tagIds: List<String>) {
        val firebaseNote = FirebaseNote(
            noteId = note.noteId,
            userId = note.userId ?: "",
            date = note.date,
            header = note.header,
            text = note.text,
            tagIds = tagIds
        )
        try {
            notesRef.document(note.noteId).set(firebaseNote).await()
        } catch (e: Exception) {
            // Обработка ошибок
        }
    }

    // Suspend-функция для загрузки тегов
    suspend fun uploadTag(tag: TagsEntity) {
        val firebaseTag = FirebaseTag(
            tagId = tag.tagId,
            userId = tag.userId ?: "",
            text = tag.text
        )
        try {
            tagsRef.document(tag.tagId).set(firebaseTag).await()
        } catch (e: Exception) {
            // Обработка ошибок
        }
    }

    // Слушатель для синхронизации заметок в реальном времени
    fun observeNotes(userId: String, onNotesReceived: (List<FirebaseNote>) -> Unit) {
        notesRef.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val notes = snapshot?.toObjects(FirebaseNote::class.java) ?: emptyList()
                onNotesReceived(notes)
            }
    }

    // Слушатель для синхронизации тегов в реальном времени
    fun observeTags(userId: String, onTagsReceived: (List<FirebaseTag>) -> Unit) {
        tagsRef.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val tags = snapshot?.toObjects(FirebaseTag::class.java) ?: emptyList()
                onTagsReceived(tags)
            }
    }
}