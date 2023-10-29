package com.example.skyr.note.api

import com.example.skyr.api.ApiController
import com.example.skyr.api.IdResponse
import com.example.skyr.note.Note
import com.example.skyr.note.NoteService
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class NoteController(val noteService: NoteService) : ApiController {

    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/notes"], consumes = ["application/json"], produces = ["application/json"])
    fun addNote(@Valid @RequestBody request: AddNoteRequest): IdResponse<UUID> {
        val noteId = noteService.addNote(request.title, request.content)
        return IdResponse(noteId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = ["/notes/{noteId}"])
    fun deleteNote(@PathVariable noteId: UUID) {
        noteService.deleteNote(noteId)
    }

    @GetMapping(value = ["/notes/{noteId}"], produces = ["application/json"])
    fun getNote(@PathVariable noteId: UUID): NoteResponse {
        val note = noteService.getNote(noteId)
        return NoteResponse(note.title, note.content)
    }

    @Validated
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = ["/notes/{noteId}"], consumes = ["application/json"])
    fun updateNote(@PathVariable noteId: UUID, @Valid @RequestBody request: UpdateNoteRequest) {
        noteService.updateNote(noteId, request.title, request.content)
    }

    data class AddNoteRequest(
        @field:Size(max = Note.TITLE_MAX_LENGHT) val title: String,
        @field:Size(max = Note.CONTENT_MAX_LENGTH) val content: String
    )

    data class UpdateNoteRequest(
        @field:Size(max = Note.TITLE_MAX_LENGHT) val title: String?,
        @field:Size(max = Note.CONTENT_MAX_LENGTH) val content: String?
    )

    data class NoteResponse(val title: String, val content: String)
}
