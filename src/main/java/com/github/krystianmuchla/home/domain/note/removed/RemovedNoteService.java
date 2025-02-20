package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteNotUpdatedException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.removed.RemovedNotePersistence;

public class RemovedNoteService {
    public static final RemovedNoteService INSTANCE = new RemovedNoteService();

    public void update(RemovedNote removedNote) throws RemovedNoteNotUpdatedException {
        removedNote.updateModificationTime();
        removedNote.updateVersion();
        var result = Transaction.run(() -> RemovedNotePersistence.update(removedNote));
        if (!result) {
            throw new RemovedNoteNotUpdatedException();
        }
    }

    public void delete(RemovedNote removedNote) throws RemovedNoteNotUpdatedException {
        var result = Transaction.run(() -> RemovedNotePersistence.delete(removedNote));
        if (!result) {
            throw new RemovedNoteNotUpdatedException();
        }
    }
}
