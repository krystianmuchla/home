package com.example.skyr.note.sync

import com.example.skyr.exception.ClientErrorException
import com.example.skyr.exception.NotFoundException
import com.example.skyr.exception.ServiceErrorException
import com.example.skyr.note.Note
import com.example.skyr.note.NoteDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class NotesSyncService(private val notesSyncDao: NotesSyncDao, private val noteDao: NoteDao) {

    @Transactional
    fun sync(externalSyncId: UUID, externalSyncTime: Instant, externalNotes: List<Note>): NotesSyncResult {
        validate(externalSyncTime, externalNotes)
        var notesSync = getWithLock()
        validate(externalSyncTime, notesSync.syncTime)
        if (notesSync.syncId == externalSyncId) {
            return notesSyncResult(notesSync)
        }
        var notes = getNotesWithLock(externalSyncTime)
        var externalNoteAdded = false
        val excludedNotes = ArrayList<UUID>()
        externalNotes.forEach {
            val note = notes[it.id]
            if (note == null) {
                noteDao.create(it)
                externalNoteAdded = true
            } else if (note.modificationTime < it.modificationTime) {
                noteDao.update(it)
                excludedNotes.add(it.id)
            }
        }
        if (externalNoteAdded || excludedNotes.isNotEmpty()) {
            notes = notes.toMutableMap()
            excludedNotes.forEach { notes.remove(it) }
            notesSync =
                NotesSync(syncTime = resolveSyncTime(notes.values, externalNotes))
            notesSyncDao.update(notesSync)
        }
        return notesSyncResult(notesSync, notes.values.toList())
    }

    private fun getWithLock(): NotesSync {
        return notesSyncDao.read(writeLock = true) ?: throw NotFoundException("NoteSync not found")
    }

    private fun getNotesWithLock(externalSyncTime: Instant) =
        noteDao.read(externalSyncTime, writeLock = true).associateBy { it.id }

    private fun validate(externalSyncTime: Instant, externalNotes: List<Note>) {
        externalNotes.forEach { if (it.modificationTime <= externalSyncTime) throw ClientErrorException("Provided note's modification time is not after sync time") }
    }

    private fun validate(externalSyncTime: Instant, syncTime: Instant) {
        if (externalSyncTime.isAfter(syncTime)) {
            throw ClientErrorException("Provided sync time is after persisted sync time")
        }
    }

    private fun resolveSyncTime(notes: Collection<Note>, externalNotes: List<Note>): Instant {
        val modificationTime = notes.maxByOrNull { it.modificationTime }
        val externalModificationTime = externalNotes.maxByOrNull { it.modificationTime }
        if (modificationTime == null && externalModificationTime == null) {
            throw ServiceErrorException("Notes and external notes cannot be null at the same time")
        }
        return maxOf(
            modificationTime?.modificationTime ?: Instant.MIN,
            externalModificationTime?.modificationTime ?: Instant.MIN
        )
    }
}
