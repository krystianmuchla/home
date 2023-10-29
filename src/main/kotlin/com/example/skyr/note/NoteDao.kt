package com.example.skyr.note

import com.example.skyr.Dao
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class NoteDao(val jdbcTemplate: JdbcTemplate) : Dao() {

    fun add(id: UUID, title: String, content: String) {
        jdbcTemplate.update("INSERT INTO note VALUES (UUID_TO_BIN(?), ?, ?)", id.toString(), title, content)
    }

    fun delete(id: UUID): Boolean {
        val result = jdbcTemplate.update("DELETE FROM note WHERE id = UUID_TO_BIN(?)", id.toString())
        return resolveResult(result)
    }

    fun get(noteId: UUID): Note? {
        val result = jdbcTemplate.query(
            "SELECT BIN_TO_UUID(id) AS id, title, content FROM note WHERE id = UUID_TO_BIN(?)",
            { resultSet, _ -> Note.of(resultSet) },
            noteId.toString()
        )
        return resolveSingleResult(result)
    }

    fun updateTitle(noteId: UUID, title: String) {
        jdbcTemplate.update("UPDATE note SET title = ? WHERE id = UUID_TO_BIN(?)", title, noteId.toString())
    }

    fun updateContent(noteId: UUID, content: String) {
        jdbcTemplate.update("UPDATE note SET content = ? WHERE id = UUID_TO_BIN(?)", content, noteId.toString())
    }

    fun update(noteId: UUID, title: String, content: String) {
        jdbcTemplate.update(
            "UPDATE note SET title = ?, content = ? WHERE id = UUID_TO_BIN(?)",
            title,
            content,
            noteId.toString()
        )
    }
}
