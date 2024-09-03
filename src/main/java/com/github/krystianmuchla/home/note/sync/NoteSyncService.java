package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.note.NotePersistence;
import com.github.krystianmuchla.home.note.NoteService;
import com.github.krystianmuchla.home.note.removed.RemovedNote;
import com.github.krystianmuchla.home.note.removed.RemovedNotePersistence;
import com.github.krystianmuchla.home.note.removed.RemovedNoteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteSyncService {
    public static List<Note> sync(UUID userId, List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return NotePersistence.read(userId);
        }
        var notes = toMap(NotePersistence.readForUpdate(userId), RemovedNotePersistence.readForUpdate(userId));
        var excludedNotes = sync(notes, externalNotes);
        excludedNotes.forEach(notes::remove);
        return List.copyOf(notes.values());
    }

    private static List<UUID> sync(Map<UUID, Note> notes, List<Note> externalNotes) {
        var excludedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id;
            var note = notes.get(id);
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
                            RemovedNoteService.update(externalNote.asRemovedNote());
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

    private static Map<UUID, Note> toMap(List<Note> notes, List<RemovedNote> removedNotes) {
        return Stream.concat(notes.stream(), removedNotes.stream().map(RemovedNote::asNote))
            .collect(Collectors.toMap(note -> note.id, Function.identity()));
    }
}
