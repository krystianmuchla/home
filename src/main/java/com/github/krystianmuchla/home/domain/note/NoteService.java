package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.note.error.NoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.NotePersistence;

import java.util.UUID;

public class NoteService {
    public static final NoteService INSTANCE = new NoteService();

    public void create(UUID id, UUID userId, String title, String content, Time contentsModificationTime) throws NoteValidationException {
        var note = new Note(id, userId, title, content, contentsModificationTime);
        Transaction.run(() -> NotePersistence.create(note));
    }

    public void update(Note note) throws NoteValidationException, NoteNotUpdatedException {
        note.updateModificationTime(new Time());
        note.updateVersion(note.version + 1);
        var result = Transaction.run(() -> NotePersistence.update(note));
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }

    public void delete(Note note) throws NoteNotUpdatedException {
        var result = Transaction.run(() -> NotePersistence.delete(note));
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }
}
