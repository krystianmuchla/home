package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.domain.note.error.NoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNoteService;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
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

    public List<NoteDto> sync(UUID userId, List<NoteDto> externalNotes) throws NoteValidationException, RemovedNoteValidationException {
        if (externalNotes == null || externalNotes.isEmpty()) {
            return notesToUpdate(NotePersistence.read(userId), RemovedNotePersistence.read(userId));
        }
        var notes = CollectionService.toMap(note -> note.id, NotePersistence.read(userId));
        var removedNotes = CollectionService.toMap(removedNote -> removedNote.id, RemovedNotePersistence.read(userId));
        Transaction.run(() -> {
            syncNotes(userId, notes, removedNotes, externalNotes).forEach(notes::remove);
            syncRemovedNotes(userId, removedNotes, notes, externalNotes).forEach(removedNotes::remove);
        });
        return notesToUpdate(notes.values(), removedNotes.values());
    }

    private List<UUID> syncNotes(
        UUID userId,
        Map<UUID, Note> notes,
        Map<UUID, RemovedNote> removedNotes,
        List<NoteDto> externalNotes
    ) throws NoteValidationException {
        var excludedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id();
            try {
                syncNote(userId, notes.get(id), removedNotes.get(id), externalNote).ifPresent(excludedNotes::add);
            } catch (NoteNotUpdatedException exception) {
                throw new IllegalStateException(exception);
            }
        }
        return excludedNotes;
    }

    private List<UUID> syncRemovedNotes(
        UUID userId,
        Map<UUID, RemovedNote> removedNotes,
        Map<UUID, Note> notes,
        List<NoteDto> externalNotes
    ) throws RemovedNoteValidationException {
        var excludedRemovedNotes = new ArrayList<UUID>();
        for (var externalNote : externalNotes) {
            var id = externalNote.id();
            try {
                syncRemovedNote(userId, removedNotes.get(id), notes.get(id), externalNote).ifPresent(excludedRemovedNotes::add);
            } catch (RemovedNoteNotUpdatedException exception) {
                throw new IllegalStateException(exception);
            }
        }
        return excludedRemovedNotes;
    }

    private Optional<UUID> syncNote(
        UUID userId,
        Note note,
        RemovedNote removedNote,
        NoteDto externalNote
    ) throws NoteValidationException, NoteNotUpdatedException {
        if (note == null) {
            if (externalNote.hasContent()) {
                if (removedNote == null) {
                    createNote(userId, externalNote);
                } else {
                    if (removedNote.removalTime.isBefore(externalNote.contentsModificationTime())) {
                        createNote(userId, externalNote);
                    }
                }
            }
        } else {
            if (note.contentsModificationTime.isBefore(externalNote.contentsModificationTime())) {
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
        UUID userId,
        RemovedNote removedNote,
        Note note,
        NoteDto externalNote
    ) throws RemovedNoteValidationException, RemovedNoteNotUpdatedException {
        if (removedNote == null) {
            if (!externalNote.hasContent()) {
                if (note == null) {
                    createRemovedNote(userId, externalNote);
                } else {
                    if (note.contentsModificationTime.isBefore(externalNote.contentsModificationTime())) {
                        createRemovedNote(userId, externalNote);
                    }
                }
            }
        } else {
            if (removedNote.removalTime.isBefore(externalNote.contentsModificationTime())) {
                if (externalNote.hasContent()) {
                    removedNoteService.delete(removedNote);
                } else {
                    update(removedNote, externalNote);
                }
                return Optional.of(externalNote.id());
            }
        }
        return Optional.empty();
    }

    private void createNote(UUID userId, NoteDto externalNote) throws NoteValidationException {
        noteService.create(
            externalNote.id(),
            userId,
            externalNote.title(),
            externalNote.content(),
            externalNote.contentsModificationTime()
        );
    }

    private void createRemovedNote(UUID userId, NoteDto externalNote) throws RemovedNoteValidationException {
        removedNoteService.create(
            externalNote.id(),
            userId,
            externalNote.contentsModificationTime()
        );
    }

    private void update(Note note, NoteDto externalNote) throws NoteValidationException, NoteNotUpdatedException {
        if (!Objects.equals(note.title, externalNote.title())) {
            note.updateTitle(externalNote.title());
        }
        if (!Objects.equals(note.content, externalNote.content())) {
            note.updateContent(externalNote.content());
        }
        note.updateContentsModificationTime(externalNote.contentsModificationTime());
        noteService.update(note);
    }

    private void update(RemovedNote removedNote, NoteDto externalNote) throws RemovedNoteValidationException, RemovedNoteNotUpdatedException {
        removedNote.updateRemovalTime(externalNote.contentsModificationTime());
        removedNoteService.update(removedNote);
    }

    private static List<NoteDto> notesToUpdate(Collection<Note> notes, Collection<RemovedNote> removedNotes) {
        return Stream.concat(
            notes.stream().map(note -> new NoteDto(note.id, note.title, note.content, note.contentsModificationTime)),
            removedNotes.stream().map(removedNote -> new NoteDto(removedNote.id, removedNote.removalTime))
        ).toList();
    }
}
