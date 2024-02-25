package com.github.krystianmuchla.home.mnemo.grave;

public class NoteGraveService {
    public static final NoteGraveService INSTANCE = new NoteGraveService();

    private final NoteGraveDao noteGraveDao = NoteGraveDao.INSTANCE;

    public void update(final NoteGrave noteGrave) {
        final var result = noteGraveDao.update(noteGrave);
        if (!result) {
            throw new IllegalArgumentException();
        }
    }
}
