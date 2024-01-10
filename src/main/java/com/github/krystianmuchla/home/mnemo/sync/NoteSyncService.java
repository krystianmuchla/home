package com.github.krystianmuchla.home.mnemo.sync;

import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteDao;
import com.github.krystianmuchla.home.mnemo.NoteService;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;

import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteSyncService {
    private static final Map<Connection, NoteSyncService> INSTANCES = new HashMap<>();
    private final NoteDao noteDao;
    private final NoteGraveDao noteGraveDao;
    private final NoteService noteService;

    public NoteSyncService(final Connection dbConnection) {
        noteDao = NoteDao.getInstance(dbConnection);
        noteGraveDao = NoteGraveDao.getInstance(dbConnection);
        noteService = NoteService.getInstance(dbConnection);
    }

    public List<Note> sync(final List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return noteDao.read();
        }
        final var notes = toMap(noteDao.readWithLock(), noteGraveDao.readWithLock());
        final var overriddenNotes = sync(notes, externalNotes);
        overriddenNotes.forEach(notes::remove);
        return List.copyOf(notes.values());
    }

    private List<UUID> sync(final Map<UUID, Note> notes, final List<Note> externalNotes) {
        final var overriddenNotes = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final UUID id = externalNote.id();
            final var note = notes.get(id);
            if (note == null) {
                if (externalNote.hasContent()) {
                    noteService.create(externalNote);
                }
            } else if (note.modificationTime().isBefore(externalNote.modificationTime())) {
                if (externalNote.hasContent()) {
                    if (note.hasContent()) {
                        noteService.update(externalNote);
                    } else {
                        noteService.create(externalNote);
                    }
                } else {
                    if (note.hasContent()) {
                        noteService.delete(externalNote);
                    }
                }
                overriddenNotes.add(id);
            }
        }
        return overriddenNotes;
    }

    private Map<UUID, Note> toMap(final List<Note> notes, final List<NoteGrave> noteGraves) {
        return Stream.concat(
            notes.stream(),
            noteGraves.stream().map(NoteGrave::toNote)
        ).collect(Collectors.toMap(Note::id, Function.identity()));
    }

    public static NoteSyncService getInstance(final Connection dbConnection) {
        var instance = INSTANCES.get(dbConnection);
        if (instance == null) {
            instance = new NoteSyncService(dbConnection);
            INSTANCES.put(dbConnection, instance);
        }
        return instance;
    }
}
