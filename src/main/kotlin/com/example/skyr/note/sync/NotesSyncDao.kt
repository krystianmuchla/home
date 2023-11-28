package com.example.skyr.note.sync

import com.example.skyr.Dao
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class NotesSyncDao(private val jdbcTemplate: JdbcTemplate) : Dao() {

    companion object {
        const val NOTES_SYNC = "notes_sync"
    }

    fun read(writeLock: Boolean = false): NotesSync? {
        val result = jdbcTemplate.query(
            "SELECT ${NotesSync.SYNC_ID}, ${NotesSync.SYNC_TIME} FROM $NOTES_SYNC" + forUpdate(writeLock)
        ) { resultSet, _ -> notesSync(resultSet) }
        return resolveSingleResult(result)
    }

    fun update(notesSync: NotesSync) {
        jdbcTemplate.update(
            "UPDATE $NOTES_SYNC SET ${NotesSync.SYNC_ID} = ?, ${NotesSync.SYNC_TIME} = ?",
            notesSync.syncId.toString(),
            timestamp(notesSync.syncTime).toString()
        )
    }
}
