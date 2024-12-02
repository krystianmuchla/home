package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.domain.note.exception.NoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNoteService;
import com.github.krystianmuchla.home.infrastructure.persistence.note.NotePersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.note.RemovedNotePersistence;

import java.util.*;
import java.util.stream.Stream;

public class NoteSyncService {
    public static List<Note> sync(UUID userId, List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return notesToUpdate(NotePersistence.read(userId), RemovedNotePersistence.read(userId));
        }
        Map<UUID, Note> notes = CollectionService.toMap(note -> note.id, NotePersistence.read(userId));
        Map<UUID, RemovedNote> removedNotes = CollectionService.toMap(removedNote -> removedNote.id, RemovedNotePersistence.read(userId));
        syncNotes(notes, removedNotes, externalNotes).forEach(notes::remove);
        syncRemovedNotes(removedNotes, notes, externalNotes).forEach(removedNotes::remove);
        return notesToUpdate(notes.values(), removedNotes.values());
    }

    private static List<UUID> syncNotes(
        Map<UUID, Note> notes,
        Map<UUID, RemovedNote> removedNotes,
        List<Note> externalNotes
    ) {
        var excludedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id;
            try {
                syncNote(notes.get(id), removedNotes.get(id), externalNote).ifPresent(excludedNotes::add);
            } catch (NoteNotUpdatedException exception) {
                throw new RuntimeException(exception);
            }
        }
        return excludedNotes;
    }

    private static List<UUID> syncRemovedNotes(
        Map<UUID, RemovedNote> removedNotes,
        Map<UUID, Note> notes,
        List<Note> externalNotes
    ) {
        var excludedRemovedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id;
            try {
                syncRemovedNote(removedNotes.get(id), notes.get(id), externalNote).ifPresent(excludedRemovedNotes::add);
            } catch (NoteNotUpdatedException exception) {
                throw new RuntimeException(exception);
            }
        }
        return excludedRemovedNotes;
    }

    private static Optional<UUID> syncNote(
        Note note,
        RemovedNote removedNote,
        Note externalNote
    ) throws NoteNotUpdatedException {
        if (note == null) {
            if (externalNote.hasContent()) {
                if (removedNote == null) {
                    NotePersistence.create(externalNote);
                } else {
                    if (removedNote.removalTime.isBefore(externalNote.contentsModificationTime)) {
                        NotePersistence.create(externalNote);
                    }
                }
            }
        } else {
            if (note.contentsModificationTime.isBefore(externalNote.contentsModificationTime)) {
                if (externalNote.hasContent()) {
                    update(note, externalNote);
                } else {
                    NoteService.delete(note);
                }
                return Optional.of(note.id);
            }
        }
        return Optional.empty();
    }

    private static Optional<UUID> syncRemovedNote(
        RemovedNote removedNote,
        Note note,
        Note externalNote
    ) throws NoteNotUpdatedException {
        if (removedNote == null) {
            if (!externalNote.hasContent()) {
                if (note == null) {
                    RemovedNotePersistence.create(externalNote.asRemovedNote());
                } else {
                    if (note.contentsModificationTime.isBefore(externalNote.contentsModificationTime)) {
                        RemovedNotePersistence.create(externalNote.asRemovedNote());
                    }
                }
            }
        } else {
            if (removedNote.removalTime.isBefore(externalNote.contentsModificationTime)) {
                if (externalNote.hasContent()) {
                    RemovedNoteService.delete(removedNote);
                } else {
                    update(removedNote, externalNote);
                }
                return Optional.of(externalNote.id);
            }
        }
        return Optional.empty();
    }

    private static void update(Note note, Note externalNote) throws NoteNotUpdatedException {
        if (!Objects.equals(note.title, externalNote.title)) {
            note.updateTitle(externalNote.title);
        }
        if (!Objects.equals(note.content, externalNote.content)) {
            note.updateContent(externalNote.content);
        }
        note.updateContentsModificationTime(externalNote.contentsModificationTime);
        NoteService.update(note);
    }

    private static void update(RemovedNote removedNote, Note externalNote) throws NoteNotUpdatedException {
        removedNote.updateRemovalTime(externalNote.contentsModificationTime);
        RemovedNoteService.update(removedNote);
    }

    private static List<Note> notesToUpdate(Collection<Note> notes, Collection<RemovedNote> removedNotes) {
        return Stream.concat(
            notes.stream(),
            removedNotes.stream().map(RemovedNote::asNote)
        ).toList();
    }
}
