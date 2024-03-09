package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.error.exception.MissingResourceException;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;

import java.util.UUID;

public class NoteService {
    public static final NoteService INSTANCE = new NoteService();

    private final NoteDao noteDao = NoteDao.INSTANCE;
    private final NoteGraveDao noteGraveDao = NoteGraveDao.INSTANCE;

    public UUID create(final UUID userId, final String title, final String content) {
        final var id = UUID.randomUUID();
        final var creationTime = InstantFactory.create();
        final var note = new Note(id, userId, title, content, creationTime);
        noteDao.create(note);
        return note.id();
    }

    public void create(final Note note) {
        noteGraveDao.delete(note.asNoteGrave());
        noteDao.create(note);
    }

    public Note read(final UUID id, final UUID userId) {
        final var result = noteDao.read(id, userId);
        if (result == null) {
            throw new MissingResourceException();
        }
        return result;
    }

    public void update(final UUID id, final UUID userId, final String title, final String content) {
        final var result = noteDao.update(id, userId, title, content, InstantFactory.create());
        if (!result) {
            throw new MissingResourceException();
        }
    }

    public void update(final Note note) {
        final var result = noteDao.update(
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

    public void delete(final UUID id, final UUID userId) {
        final var result = noteDao.delete(id, userId);
        if (!result) {
            throw new MissingResourceException();
        }
        final var noteGrave = new NoteGrave(id, userId);
        noteGraveDao.create(noteGrave);
    }

    public void delete(final Note note) {
        final var result = noteDao.delete(note);
        if (!result) {
            throw new MissingResourceException();
        }
        noteGraveDao.create(note.asNoteGrave());
    }
}
