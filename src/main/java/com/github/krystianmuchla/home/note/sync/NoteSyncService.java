package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.note.NotePersistence;
import com.github.krystianmuchla.home.note.NoteService;
import com.github.krystianmuchla.home.note.grave.NoteGrave;
import com.github.krystianmuchla.home.note.grave.NoteGravePersistence;
import com.github.krystianmuchla.home.note.grave.NoteGraveService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteSyncService {
    public static List<Note> sync(final UUID userId, final List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return NotePersistence.read(userId);
        }
        final var notes = toMap(NotePersistence.readForUpdate(userId), NoteGravePersistence.readForUpdate(userId));
        final var excludedNotes = sync(notes, externalNotes);
        excludedNotes.forEach(notes::remove);
        return List.copyOf(notes.values());
    }

    private static List<UUID> sync(final Map<UUID, Note> notes, final List<Note> externalNotes) {
        final var excludedNotes = new ArrayList<UUID>();
        for (final var externalNote : externalNotes) {
            final var id = externalNote.id;
            final var note = notes.get(id);
            if (note == null) {
                if (externalNote.hasContent()) {
                    NoteService.create(externalNote);
                }
            } else {
                if (note.modificationTime.isBefore(externalNote.modificationTime)) {
                    if (externalNote.hasContent()) {
                        if (note.hasContent()) {
                            NoteService.update(externalNote);
                        } else {
                            NoteService.create(externalNote);
                        }
                    } else {
                        if (note.hasContent()) {
                            NoteService.delete(externalNote);
                        } else {
                            NoteGraveService.update(externalNote.asNoteGrave());
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

    private static Map<UUID, Note> toMap(final List<Note> notes, final List<NoteGrave> noteGraves) {
        return Stream.concat(notes.stream(), noteGraves.stream().map(NoteGrave::asNote))
            .collect(Collectors.toMap(note -> note.id, Function.identity()));
    }
}
