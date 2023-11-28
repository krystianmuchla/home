package com.example.skyr.note

import com.example.skyr.getInstant
import com.example.skyr.getUuid
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class Note(id: UUID, title: String, content: String, creationTime: Instant, modificationDate: Instant) {

    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val CREATION_TIME = "creation_time"
        const val MODIFICATION_TIME = "modification_time"
        const val TITLE_MAX_LENGTH = 255
        const val CONTENT_MAX_LENGTH = 65535
    }

    val id: UUID
    val title: String
    val content: String
    val creationTime: Instant
    val modificationTime: Instant

    init {
        if (title.length > TITLE_MAX_LENGTH) {
            throw IllegalArgumentException("Note title exceeded max length of $TITLE_MAX_LENGTH")
        }
        if (content.length > CONTENT_MAX_LENGTH) {
            throw IllegalArgumentException("Note content exceeded max length of $CONTENT_MAX_LENGTH")
        }
        this.id = id
        this.title = title
        this.content = content
        this.creationTime = creationTime
        this.modificationTime = modificationDate
    }
}

fun note(resultSet: ResultSet): Note {
    val id = resultSet.getUuid(Note.ID)
    val title = resultSet.getString(Note.TITLE)
    val content = resultSet.getString(Note.CONTENT)
    val creationTime = resultSet.getInstant(Note.CREATION_TIME)
    val modificationTime = resultSet.getInstant(Note.MODIFICATION_TIME)
    return Note(id, title, content, creationTime, modificationTime)
}
