package com.example.skyr.note

import com.example.skyr.pagination.PaginatedResult
import com.example.skyr.pagination.Pagination
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(val noteDao: NoteDao) {

    fun addNote(title: String, content: String): UUID {
        val id = UUID.randomUUID()
        noteDao.add(id, title, content)
        return id
    }

    fun deleteNote(noteId: UUID) {
        val result = noteDao.delete(noteId)
        if (!result) {
            throw MissingResourceException("Note not found", Note::class.java.simpleName, noteId.toString())
        }
    }

    fun getNote(noteId: UUID): Note {
        return noteDao.get(noteId) ?: throw MissingResourceException(
            "Note not found",
            Note::class.java.simpleName,
            noteId.toString()
        )
    }

    fun getNotes(pagination: Pagination): PaginatedResult<Note> {
        return noteDao.get(pagination)
    }

    fun updateNote(noteId: UUID, title: String?, content: String?) {
        if (title != null && content != null) {
            noteDao.update(noteId, title, content)
        } else if (title != null) {
            noteDao.updateTitle(noteId, title)
        } else if (content != null) {
            noteDao.updateContent(noteId, content)
        }
    }
}
