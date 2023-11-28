package com.example.skyr.note.api

import com.example.skyr.api.IdResponse
import com.example.skyr.note.Note
import com.example.skyr.note.NoteService
import com.example.skyr.pagination.api.PaginatedResponse
import com.example.skyr.pagination.api.PaginationRequest
import com.example.skyr.pagination.api.paginatedResponse
import com.example.skyr.pagination.pagination
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class NoteController(private val noteService: NoteService) {

    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/api/notes"], consumes = ["application/json"], produces = ["application/json"])
    fun postNote(@Valid @RequestBody request: AddNoteRequest): IdResponse<UUID> {
        val noteId = noteService.add(request.title, request.content)
        return IdResponse(noteId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = ["/api/notes/{noteId}"])
    fun deleteNote(@PathVariable noteId: UUID) {
        noteService.remove(noteId)
    }

    @GetMapping(value = ["/api/notes/{noteId}"], produces = ["application/json"])
    fun getNote(@PathVariable noteId: UUID): NoteResponse {
        val note = noteService.get(noteId)
        return NoteResponse(
            title = note.title,
            content = note.content,
            creationDate = note.creationTime,
            modificationDate = note.modificationTime
        )
    }

    @GetMapping(value = ["/api/notes"], produces = ["application/json"])
    fun getNotes(request: PaginationRequest): PaginatedResponse<NoteResponse> {
        val paginatedResult = noteService.get(pagination(request))
        return paginatedResponse(paginatedResult) { note: Note -> noteResponse(note) }
    }

    @Validated
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = ["/api/notes/{noteId}"], consumes = ["application/json"])
    fun putNote(@PathVariable noteId: UUID, @Valid @RequestBody request: UpdateNoteRequest) {
        noteService.update(noteId, request.title, request.content)
    }

    data class AddNoteRequest(
        @field:Size(max = Note.TITLE_MAX_LENGTH) val title: String,
        @field:Size(max = Note.CONTENT_MAX_LENGTH) val content: String
    )

    data class UpdateNoteRequest(
        @field:Size(max = Note.TITLE_MAX_LENGTH) val title: String?,
        @field:Size(max = Note.CONTENT_MAX_LENGTH) val content: String?
    )
}
