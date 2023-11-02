package com.example.skyr.note

import com.example.skyr.Dao
import com.example.skyr.pagination.PaginatedResult
import com.example.skyr.pagination.Pagination
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class NoteDao(val jdbcTemplate: JdbcTemplate) : Dao() {

    fun create(id: UUID, title: String, content: String) {
        jdbcTemplate.update("INSERT INTO note VALUES (UUID_TO_BIN(?), ?, ?)", id.toString(), title, content)
    }

    fun delete(id: UUID): Boolean {
        val result = jdbcTemplate.update("DELETE FROM note WHERE id = UUID_TO_BIN(?)", id.toString())
        return resolveResult(result)
    }

    fun read(noteId: UUID): Note? {
        val result = jdbcTemplate.query(
            "SELECT BIN_TO_UUID(id) AS id, title, content FROM note WHERE id = UUID_TO_BIN(?)",
            { resultSet, _ -> note(resultSet) },
            noteId.toString()
        )
        return resolveSingleResult(result)
    }

    fun read(pagination: Pagination): PaginatedResult<Note> {
        val result = jdbcTemplate.query(
            "SELECT BIN_TO_UUID(id) AS id, title, content FROM note LIMIT ? OFFSET ?",
            { resultSet, _ -> note(resultSet) },
            limit(pagination.pageSize),
            offset(pagination.pageNumber, pagination.pageSize)
        )
        return resolvePaginatedResult(pagination, result)
    }

    fun updateTitle(id: UUID, title: String) {
        jdbcTemplate.update("UPDATE note SET title = ? WHERE id = UUID_TO_BIN(?)", title, id.toString())
    }

    fun updateContent(id: UUID, content: String) {
        jdbcTemplate.update("UPDATE note SET content = ? WHERE id = UUID_TO_BIN(?)", content, id.toString())
    }

    fun update(id: UUID, title: String, content: String) {
        jdbcTemplate.update(
            "UPDATE note SET title = ?, content = ? WHERE id = UUID_TO_BIN(?)",
            title,
            content,
            id.toString()
        )
    }
}
