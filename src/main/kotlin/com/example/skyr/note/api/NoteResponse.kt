package com.example.skyr.note.api

import com.example.skyr.note.Note
import java.time.Instant
import java.util.*

data class NoteResponse(
    val id: UUID? = null,
    val title: String,
    val content: String,
    val creationDate: Instant,
    val modificationDate: Instant
)

fun noteResponse(note: Note) =
    NoteResponse(
        note.id,
        note.title,
        note.content,
        note.creationTime,
        note.modificationTime
    )

fun noteResponses(notes: List<Note>?) = notes?.let { it -> it.map { noteResponse(it) } } ?: emptyList()
