package com.github.krystianmuchla.home.mnemo;

import java.util.UUID;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteService {
    public static final NoteService INSTANCE = new NoteService();

    private final NoteDao noteDao = NoteDao.INSTANCE;
    private final NoteGraveDao noteGraveDao = NoteGraveDao.INSTANCE;

    public UUID create(final String title, final String content) {
        final var id = UUID.randomUUID();
        final var creationTime = InstantFactory.create();
        noteDao.create(id, title, content, creationTime, creationTime);
        return id;
    }

    public void create(final Note note) {
        noteGraveDao.delete(note.id());
        noteDao.create(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    public void update(final UUID id, final String title, final String content) {
        final var result = noteDao.update(id, title, content, InstantFactory.create());
        if (!result) throw new IllegalArgumentException();
    }

    public void update(final Note note) {
        final var result = noteDao.update(note.id(), note.title(), note.content(), note.modificationTime());
        if (!result) throw new IllegalArgumentException();
    }

    public void delete(final UUID id) {
        final var result = noteDao.delete(id);
        if (!result) throw new IllegalArgumentException();
        noteGraveDao.create(id);
    }

    public void delete(final Note note) {
        final var result = noteDao.delete(note.id());
        if (!result) throw new IllegalArgumentException();
        noteGraveDao.create(note);
    }
}
