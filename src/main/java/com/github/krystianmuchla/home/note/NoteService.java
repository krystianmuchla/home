package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.note.grave.NoteGravePersistence;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.util.UUID;

public class NoteService {
    public static UUID create(UUID userId, CreateNoteRequest request) {
        var id = UUID.randomUUID();
        var creationTime = InstantFactory.create();
        var note = new Note(id, userId, request.title(), request.content(), creationTime);
        NotePersistence.create(note);
        return note.id;
    }

    public static void create(Note note) {
        NoteGravePersistence.delete(note.asNoteGrave());
        NotePersistence.create(note);
    }

    public static void update(UUID userId, UpdateNoteRequest request) {
        var note = NotePersistence.readForUpdate(userId, request.id());
        if (note == null) {
            throw new NotFoundException();
        }
        note.title = request.title();
        note.content = request.content();
        note.modificationTime = InstantFactory.create();
        update(note);
    }

    public static void update(Note note) {
        var result = NotePersistence.update(note);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(UUID userId, NoteFilterRequest request) {
        if (request.isEmpty()) {
            throw new BadRequestException();
        }
        var notes = NotePersistence.readForUpdate(userId, request.ids());
        if (notes.isEmpty()) {
            throw new NotFoundException();
        }
        notes.stream().peek(note -> note.modificationTime = InstantFactory.create()).forEach(NoteService::delete);
    }

    public static void delete(Note note) {
        var result = NotePersistence.delete(note);
        if (!result) {
            throw new NotFoundException();
        }
        NoteGravePersistence.create(note.asNoteGrave());
    }
}
