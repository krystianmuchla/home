package com.example.skyr.note.sync.api

import com.example.skyr.note.Note
import com.example.skyr.note.api.NoteResponse
import com.example.skyr.note.api.noteResponses
import com.example.skyr.note.sync.NotesSyncService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

@RestController
class NotesSyncController(private val notesSyncService: NotesSyncService) {

    @PutMapping(value = ["/api/notes/sync"], consumes = ["application/json"], produces = ["application/json"])
    fun syncNotes(@RequestBody request: SyncNotesRequest): NotesSyncResponse {
        val notesSyncResult =
            notesSyncService.sync(request.syncId, request.syncTime.toInstant(), notes(request.modifiedNotes))
        return NotesSyncResponse(
            notesSyncResult.syncId,
            notesSyncResult.syncTime,
            noteResponses(notesSyncResult.modifiedNotes),
        )
    }

    data class SyncNotesRequest(
        val syncId: UUID,
        val syncTime: ZonedDateTime,
        val modifiedNotes: List<NoteRequest>
    )

    data class NoteRequest(
        val id: UUID,
        val title: String,
        val content: String,
        val creationTime: ZonedDateTime,
        val modificationTime: ZonedDateTime
    )

    private fun notes(notes: List<NoteRequest>): List<Note> {
        return notes.stream().map {
            Note(
                it.id,
                it.title,
                it.content,
                it.creationTime.toInstant(),
                it.modificationTime.toInstant(),
            )
        }.toList()
    }

    data class NotesSyncResponse(val syncId: UUID, val syncTime: Instant, val modifiedNotes: List<NoteResponse>)
}
