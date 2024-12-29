package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.domain.note.error.NoteNotUpdatedException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.NotePersistence;

public class NoteService {
    public static void update(Note note) throws NoteNotUpdatedException {
        var result = Transaction.run(() -> NotePersistence.update(note));
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }

    public static void delete(Note note) throws NoteNotUpdatedException {
        var result = Transaction.run(() -> NotePersistence.delete(note));
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }
}
