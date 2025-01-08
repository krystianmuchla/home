package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.domain.note.error.NoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNoteService;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteNotUpdatedException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.NotePersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.note.removed.RemovedNotePersistence;

import java.util.*;
import java.util.stream.Stream;

public class NoteSyncService {
    public static final NoteSyncService INSTANCE = new NoteSyncService(NoteService.INSTANCE, RemovedNoteService.INSTANCE);

    private final NoteService noteService;
    private final RemovedNoteService removedNoteService;

    public NoteSyncService(NoteService noteService, RemovedNoteService removedNoteService) {
        this.noteService = noteService;
        this.removedNoteService = removedNoteService;
    }

    public List<Note> sync(UUID userId, List<Note> externalNotes) {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return notesToUpdate(NotePersistence.read(userId), RemovedNotePersistence.read(userId));
        }
        var notes = CollectionService.toMap(note -> note.id, NotePersistence.read(userId));
        var removedNotes = CollectionService.toMap(removedNote -> removedNote.id, RemovedNotePersistence.read(userId));
        Transaction.run(() -> {
            syncNotes(notes, removedNotes, externalNotes).forEach(notes::remove);
            syncRemovedNotes(removedNotes, notes, externalNotes).forEach(removedNotes::remove);
        });
        return notesToUpdate(notes.values(), removedNotes.values());
    }

    private List<UUID> syncNotes(
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
                throw new IllegalStateException(exception);
            }
        }
        return excludedNotes;
    }

    private List<UUID> syncRemovedNotes(
        Map<UUID, RemovedNote> removedNotes,
        Map<UUID, Note> notes,
        List<Note> externalNotes
    ) {
        var excludedRemovedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id;
            try {
                syncRemovedNote(removedNotes.get(id), notes.get(id), externalNote).ifPresent(excludedRemovedNotes::add);
            } catch (RemovedNoteNotUpdatedException exception) {
                throw new IllegalStateException(exception);
            }
        }
        return excludedRemovedNotes;
    }

    private Optional<UUID> syncNote(
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
                    noteService.delete(note);
                }
                return Optional.of(note.id);
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> syncRemovedNote(
        RemovedNote removedNote,
        Note note,
        Note externalNote
    ) throws RemovedNoteNotUpdatedException {
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
                    removedNoteService.delete(removedNote);
                } else {
                    update(removedNote, externalNote);
                }
                return Optional.of(externalNote.id);
            }
        }
        return Optional.empty();
    }

    private void update(Note note, Note externalNote) throws NoteNotUpdatedException {
        if (!Objects.equals(note.title, externalNote.title)) {
            note.updateTitle(externalNote.title);
        }
        if (!Objects.equals(note.content, externalNote.content)) {
            note.updateContent(externalNote.content);
        }
        note.updateContentsModificationTime(externalNote.contentsModificationTime);
        noteService.update(note);
    }

    private void update(RemovedNote removedNote, Note externalNote) throws RemovedNoteNotUpdatedException {
        removedNote.updateRemovalTime(externalNote.contentsModificationTime);
        removedNoteService.update(removedNote);
    }

    private static List<Note> notesToUpdate(Collection<Note> notes, Collection<RemovedNote> removedNotes) {
        return Stream.concat(
            notes.stream(),
            removedNotes.stream().map(RemovedNote::asNote)
        ).toList();
    }
}
