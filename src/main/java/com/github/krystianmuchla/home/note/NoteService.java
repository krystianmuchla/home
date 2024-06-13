package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.note.grave.NoteGravePersistence;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.util.UUID;

public class NoteService {
    public static UUID create(final UUID userId, final CreateNoteRequest request) {
        final var id = UUID.randomUUID();
        final var creationTime = InstantFactory.create();
        final var note = new Note(id, userId, request.title(), request.content(), creationTime);
        NotePersistence.create(note);
        return note.id;
    }

    public static void create(final Note note) {
        NoteGravePersistence.delete(note.asNoteGrave());
        NotePersistence.create(note);
    }

    public static void update(final UUID userId, final UpdateNoteRequest request) {
        final var note = NotePersistence.readForUpdate(userId, request.id());
        if (note == null) {
            throw new NotFoundException();
        }
        note.title = request.title();
        note.content = request.content();
        note.modificationTime = InstantFactory.create();
        update(note);
    }

    public static void update(final Note note) {
        final var result = NotePersistence.update(note);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(final UUID userId, final NoteFilterRequest request) {
        if (request.isEmpty()) {
            throw new BadRequestException();
        }
        final var notes = NotePersistence.readForUpdate(userId, request.ids());
        if (notes.isEmpty()) {
            throw new NotFoundException();
        }
        notes.stream().peek(note -> note.modificationTime = InstantFactory.create()).forEach(NoteService::delete);
    }

    public static void delete(final Note note) {
        final var result = NotePersistence.delete(note);
        if (!result) {
            throw new NotFoundException();
        }
        NoteGravePersistence.create(note.asNoteGrave());
    }
}
