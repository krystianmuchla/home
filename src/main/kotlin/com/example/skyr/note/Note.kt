package com.example.skyr.note

import java.sql.ResultSet
import java.util.*

class Note(id: UUID, title: String, content: String) {

    companion object {
        const val TITLE_MAX_LENGHT = 255
        const val CONTENT_MAX_LENGTH = 65535
    }

    private val id: UUID
    val title: String
    val content: String

    init {
        if (title.length > TITLE_MAX_LENGHT) {
            throw IllegalArgumentException("Note title exceeded max length of $Note.titleMaxLenth")
        }
        if (content.length > CONTENT_MAX_LENGTH) {
            throw IllegalArgumentException("Note content exceeded max length of $Note.contentMaxLength")
        }
        this.id = id
        this.title = title
        this.content = content
    }
}

fun note(resultSet: ResultSet): Note {
    val id = UUID.fromString(resultSet.getString("id"))
    val title = resultSet.getString("title")
    val content = resultSet.getString("content")
    return Note(id, title, content)
}
