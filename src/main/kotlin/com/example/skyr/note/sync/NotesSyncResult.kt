package com.example.skyr.note.sync

import com.example.skyr.note.Note
import java.time.Instant
import java.util.*

class NotesSyncResult(val syncId: UUID, val syncTime: Instant, val modifiedNotes: List<Note>? = null)

fun notesSyncResult(notesSync: NotesSync) = NotesSyncResult(notesSync.syncId, notesSync.syncTime)

fun notesSyncResult(notesSync: NotesSync, modifiedNotes: List<Note>) =
    NotesSyncResult(notesSync.syncId, notesSync.syncTime, modifiedNotes)
