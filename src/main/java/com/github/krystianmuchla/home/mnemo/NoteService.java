package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.mnemo.grave.NoteGraveDao;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoteService {
    private final static Map<Connection, NoteService> INSTANCES = new HashMap<>();
    private final NoteDao noteDao;
    private final NoteGraveDao noteGraveDao;

    private NoteService(final Connection dbConnection) {
        this.noteDao = NoteDao.getInstance(dbConnection);
        this.noteGraveDao = NoteGraveDao.getInstance(dbConnection);
    }

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

    public static NoteService getInstance(final Connection dbConnection) {
        var instance = INSTANCES.get(dbConnection);
        if (instance == null) {
            instance = new NoteService(dbConnection);
            INSTANCES.put(dbConnection, instance);
        }
        return instance;
    }
}
