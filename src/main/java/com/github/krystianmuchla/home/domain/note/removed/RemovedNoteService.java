package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteNotUpdatedException;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.removed.RemovedNotePersistence;

import java.util.UUID;

public class RemovedNoteService {
    public static final RemovedNoteService INSTANCE = new RemovedNoteService();

    public void create(UUID id, UUID userId, Time removalTime) throws RemovedNoteValidationException {
        var removedNote = new RemovedNote(id, userId, removalTime);
        Transaction.run(() -> RemovedNotePersistence.create(removedNote));
    }

    public void update(RemovedNote removedNote) throws RemovedNoteValidationException, RemovedNoteNotUpdatedException {
        removedNote.updateModificationTime(new Time());
        removedNote.updateVersion(removedNote.version + 1);
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
