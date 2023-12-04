package com.example.skyr.note.sync;

import com.example.skyr.exception.ClientErrorException;
import com.example.skyr.exception.ServiceErrorException;
import com.example.skyr.note.Note;
import com.example.skyr.note.NoteDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotesSyncService {
    private final NotesSyncDao notesSyncDao;
    private final NoteDao noteDao;

    @Transactional
    public NotesSyncResult sync(final UUID externalSyncId, final Instant externalSyncTime, final List<Note> externalNotes) {
        validate(externalSyncTime, externalNotes);
        var notesSync = notesSyncDao.readWithLock();
        validate(externalSyncTime, notesSync.syncTime());
        if (notesSync.syncId() == externalSyncId) return NotesSyncResultFactory.create(notesSync);
        var notes = noteDao.readWithLock(externalSyncTime).stream()
                .collect(Collectors.toMap(Note::id, Function.identity()));
        var externalNoteAdded = false;
        final var excludedIds = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final var note = notes.get(externalNote.id());
            if (note == null) {
                noteDao.create(externalNote);
                externalNoteAdded = true;
            } else if (note.modificationTime().isBefore(externalNote.modificationTime())) {
                noteDao.update(externalNote);
                excludedIds.add(externalNote.id());
            }
        }
        if (externalNoteAdded || !excludedIds.isEmpty()) {
            excludedIds.forEach(notes::remove);
            notesSync = new NotesSync(UUID.randomUUID(), resolveSyncTime(notes.values(), externalNotes));
            notesSyncDao.update(notesSync);
        }
        return NotesSyncResultFactory.create(notesSync, List.copyOf(notes.values()));
    }

    private void validate(final Instant externalSyncTime, final List<Note> externalNotes) {
        externalNotes.forEach(externalNote -> {
            if (externalNote.modificationTime().isBefore(externalSyncTime)) {
                throw new ClientErrorException("Provided note modification time is before sync time");
            }
        });
    }

    private void validate(final Instant externalSyncTime, final Instant syncTime) {
        if (externalSyncTime.isAfter(syncTime)) {
            throw new ClientErrorException("Provided sync time is after persisted sync time");
        }
    }

    private Instant resolveSyncTime(final Collection<Note> notes, final List<Note> externalNotes) {
        return Stream.concat(notes.stream(), externalNotes.stream())
                .map(Note::modificationTime)
                .max(Instant::compareTo)
                .orElseThrow(() -> new ServiceErrorException("Could not extract max modification time"));
    }
}
