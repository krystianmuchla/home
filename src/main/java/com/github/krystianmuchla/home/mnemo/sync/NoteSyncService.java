package com.github.krystianmuchla.home.mnemo.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteDao;
import com.github.krystianmuchla.home.mnemo.NoteService;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveService;

public class NoteSyncService {
    public static final NoteSyncService INSTANCE = new NoteSyncService();

    private final NoteDao noteDao = NoteDao.INSTANCE;
    private final NoteGraveDao noteGraveDao = NoteGraveDao.INSTANCE;
    private final NoteService noteService = NoteService.INSTANCE;
    private final NoteGraveService noteGraveService = NoteGraveService.INSTANCE;

    public List<Note> sync(final UUID userId, final List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return noteDao.read(userId);
        }
        final var notes = toMap(noteDao.readWithLock(userId), noteGraveDao.readWithLock(userId));
        final var excludedNotes = sync(notes, externalNotes);
        excludedNotes.forEach(notes::remove);
        return List.copyOf(notes.values());
    }

    private List<UUID> sync(final Map<UUID, Note> notes, final List<Note> externalNotes) {
        final var excludedNotes = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final var id = externalNote.id();
            final var note = notes.get(id);
            if (note == null) {
                if (externalNote.hasContent()) {
                    noteService.create(externalNote);
                }
            } else {
                if (note.modificationTime().isBefore(externalNote.modificationTime())) {
                    if (externalNote.hasContent()) {
                        if (note.hasContent()) {
                            noteService.update(externalNote);
                        } else {
                            noteService.create(externalNote);
                        }
                    } else {
                        if (note.hasContent()) {
                            noteService.delete(externalNote);
                        } else {
                            noteGraveService.update(externalNote.asNoteGrave());
                        }
                    }
                    excludedNotes.add(id);
                } else if (!externalNote.hasContent() && !note.hasContent()) {
                    excludedNotes.add(id);
                }
            }
        }
        return excludedNotes;
    }

    private Map<UUID, Note> toMap(final List<Note> notes, final List<NoteGrave> noteGraves) {
        return Stream.concat(
                notes.stream(),
                noteGraves.stream().map(NoteGrave::asNote)).collect(Collectors.toMap(Note::id, Function.identity()));
    }
}
