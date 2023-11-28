package com.example.skyr.note.sync

import com.example.skyr.getInstant
import com.example.skyr.getUuid
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class NotesSync(val syncId: UUID = UUID.randomUUID(), val syncTime: Instant) {

    companion object {
        const val SYNC_ID = "sync_id"
        const val SYNC_TIME = "sync_time"
    }
}

fun notesSync(resultSet: ResultSet): NotesSync {
    val syncId = resultSet.getUuid(NotesSync.SYNC_ID)
    val syncTime = resultSet.getInstant(NotesSync.SYNC_TIME)
    return NotesSync(syncId, syncTime)
}
