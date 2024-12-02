package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.domain.note.exception.NoteNotUpdatedException;
import com.github.krystianmuchla.home.infrastructure.persistence.note.RemovedNotePersistence;

public class RemovedNoteService {
    public static void update(RemovedNote removedNote) throws NoteNotUpdatedException {
        var result = RemovedNotePersistence.update(removedNote);
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }

    public static void delete(RemovedNote removedNote) throws NoteNotUpdatedException {
        var result = RemovedNotePersistence.delete(removedNote);
        if (!result) {
            throw new NoteNotUpdatedException();
        }
    }
}
