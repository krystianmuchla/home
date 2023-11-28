package com.example.skyr.note

import com.example.skyr.Dao
import com.example.skyr.InstantFactory
import com.example.skyr.pagination.PaginatedResult
import com.example.skyr.pagination.Pagination
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class NoteDao(private val jdbcTemplate: JdbcTemplate) : Dao() {

    companion object {
        private const val NOTE = "note"
    }

    fun create(note: Note) {
        create(note.id, note.title, note.content, note.creationTime, note.modificationTime)
    }

    fun create(id: UUID, title: String, content: String) {
        val creationTime = InstantFactory.create()
        create(id, title, content, creationTime, creationTime)
    }

    fun create(id: UUID, title: String, content: String, creationTime: Instant, modificationTime: Instant) {
        jdbcTemplate.update(
            "INSERT INTO $NOTE VALUES (?, ?, ?, ?, ?)",
            id.toString(),
            title,
            content,
            timestamp(creationTime).toString(),
            timestamp(modificationTime).toString()
        )
    }

    fun delete(id: UUID): Boolean {
        val result = jdbcTemplate.update("DELETE FROM $NOTE WHERE ${Note.ID} = ?", id.toString())
        return resolveResult(result)
    }

    fun read(id: UUID): Note? {
        val result = jdbcTemplate.query(
            "SELECT * FROM $NOTE WHERE ${Note.ID} = ?",
            mapper(),
            id.toString()
        )
        return resolveSingleResult(result)
    }

    fun read(modificationTimeCursor: Instant, writeLock: Boolean = false): List<Note> {
        return jdbcTemplate.query(
            "SELECT * FROM $NOTE WHERE ${Note.MODIFICATION_TIME} > ? ORDER BY ${Note.MODIFICATION_TIME}" + forUpdate(writeLock),
            mapper(),
            timestamp(modificationTimeCursor)
        )
    }

    fun read(pagination: Pagination): PaginatedResult<Note> {
        val result = jdbcTemplate.query(
            "SELECT * FROM $NOTE LIMIT ? OFFSET ?",
            mapper(),
            limit(pagination.pageSize),
            offset(pagination.pageNumber, pagination.pageSize)
        )
        return resolvePaginatedResult(pagination, result)
    }

    fun update(note: Note) {
        update(note.id, note.title, note.content, note.creationTime, note.modificationTime)
    }

    fun update(
        id: UUID,
        title: String? = null,
        content: String? = null,
        creationTime: Instant? = null,
        modificationTime: Instant? = null
    ) {
        var parameters =
            listOfNotNull(title, content, timestamp(creationTime)?.toString(), timestamp(modificationTime)?.toString())
        if (parameters.isEmpty()) return
        parameters = parameters.toMutableList()
        parameters.add(id.toString())
        val setters = listOfNotNull(
            if (title == null) null else Note.TITLE,
            if (content == null) null else Note.CONTENT,
            if (creationTime == null) null else Note.CREATION_TIME,
            if (modificationTime == null) null else Note.MODIFICATION_TIME,
        ).stream().map { setter -> "$setter = ?" }.toList()
        jdbcTemplate.update(
            "UPDATE $NOTE SET ${setters.joinToString()} WHERE ${Note.ID} = ?",
            *(parameters.toTypedArray())
        )
    }

    private fun mapper(): RowMapper<Note> {
        return RowMapper { resultSet, _ -> note(resultSet) }
    }
}
