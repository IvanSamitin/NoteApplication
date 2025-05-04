package com.example.noteapplication8.model.entity

data class FirebaseNote(
    val noteId: String = "",
    val userId: String = "",
    val date: String = "",
    val header: String = "",
    val text: String = "",
    val tagIds: List<String> = emptyList()
)