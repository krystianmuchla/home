package com.example.skyr.note

import com.example.skyr.InstantFactory
import com.example.skyr.exception.NotFoundException
import com.example.skyr.pagination.PaginatedResult
import com.example.skyr.pagination.Pagination
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class NoteService(private val noteDao: NoteDao) {

    fun add(title: String, content: String): UUID {
        val id = UUID.randomUUID()
        noteDao.create(id, title, content)
        return id
    }

    fun remove(noteId: UUID) {
        val result = noteDao.delete(noteId)
        if (!result) throw NotFoundException("Note not found")
    }

    fun get(noteId: UUID): Note {
        return noteDao.read(noteId) ?: throw NotFoundException("Note not found")
    }

    fun get(pagination: Pagination): PaginatedResult<Note> {
        return noteDao.read(pagination)
    }

    fun update(noteId: UUID, title: String?, content: String?) {
        noteDao.update(noteId, title, content, modificationTime = InstantFactory.create())
    }
}
