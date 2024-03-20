package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.error.exception.MissingResourceException;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveSql;

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

    public static Note read(final UUID id, final UUID userId) {
        final var result = NoteSql.read(id, userId);
        if (result == null) {
            throw new MissingResourceException();
        }
        return result;
    }

    public static void update(final UUID id, final UUID userId, final String title, final String content) {
        final var result = NoteSql.update(id, userId, title, content, InstantFactory.create());
        if (!result) {
            throw new MissingResourceException();
        }
    }

    public static void update(final Note note) {
        final var result = NoteSql.update(
            note.id(),
            note.userId(),
            note.title(),
            note.content(),
            note.modificationTime()
        );
        if (!result) {
            throw new MissingResourceException();
        }
    }

    public static void delete(final UUID id, final UUID userId) {
        final var result = NoteSql.delete(id, userId);
        if (!result) {
            throw new MissingResourceException();
        }
        final var noteGrave = new NoteGrave(id, userId);
        NoteGraveSql.create(noteGrave);
    }

    public static void delete(final Note note) {
        final var result = NoteSql.delete(note);
        if (!result) {
            throw new MissingResourceException();
        }
        NoteGraveSql.create(note.asNoteGrave());
    }
}
