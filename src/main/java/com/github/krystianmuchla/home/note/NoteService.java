package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.util.InstantFactory;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.note.grave.NoteGrave;
import com.github.krystianmuchla.home.note.grave.NoteGraveSql;

import java.util.Set;
import java.util.UUID;

public class NoteService {
    public static UUID create(final UUID userId, final String title, final String content) {
        final var id = UUID.randomUUID();
        final var creationTime = InstantFactory.create();
        final var note = new Note(id, userId, title, content, creationTime);
        NoteSql.create(note);
        return note.id();
    }

    public static void create(final Note note) {
        NoteGraveSql.delete(note.asNoteGrave());
        NoteSql.create(note);
    }

    public static Note read(final UUID userId, final UUID id) {
        final var result = NoteSql.read(userId, id);
        if (result == null) {
            throw new NotFoundException();
        }
        return result;
    }

    public static void update(final UUID userId, final UUID id, final String title, final String content) {
        final var result = NoteSql.update(userId, id, title, content, InstantFactory.create());
        if (!result) {
            throw new NotFoundException();
        }
    }

    // todo pass user id
    public static void update(final Note note) {
        final var result = NoteSql.update(
            note.userId(),
            note.id(),
            note.title(),
            note.content(),
            note.modificationTime()
        );
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(final UUID userId, final Set<UUID> ids) {
        final var result = NoteSql.delete(userId, ids);
        if (!result) {
            throw new NotFoundException();
        }
        final var noteGraves = ids.stream().map(id -> new NoteGrave(id, userId)).toArray(NoteGrave[]::new);
        NoteGraveSql.create(noteGraves);
    }

    public static void delete(final UUID userId, final Note note) {
        final var result = NoteSql.delete(userId, note);
        if (!result) {
            throw new NotFoundException();
        }
        NoteGraveSql.create(note.asNoteGrave());
    }
}
