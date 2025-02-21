package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;
import com.github.krystianmuchla.home.domain.drive.directory.error.IllegalDirectoryStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.directory.DirectoryPersistence;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DirectoryService {
    public static final DirectoryService INSTANCE = new DirectoryService();

    public UUID create(UUID userId, UUID parentId, String name) throws DirectoryNotFoundException, DirectoryValidationException {
        if (parentId != null) {
            checkExistence(userId, parentId);
        }
        var directory = new Directory(userId, parentId, name);
        Transaction.run(() -> DirectoryPersistence.create(directory));
        return directory.id;
    }

    public List<Directory> list(UUID userId, UUID parentId) throws DirectoryNotFoundException {
        if (parentId != null) {
            checkExistence(userId, parentId);
        }
        return DirectoryPersistence.readByParentIdAndStatus(userId, parentId, DirectoryStatus.CREATED);
    }

    public Directory get(UUID userId, UUID directoryId) throws DirectoryNotFoundException {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new DirectoryNotFoundException();
        }
        return directory;
    }

    public void checkExistence(UUID userId, UUID directoryId) throws DirectoryNotFoundException {
        get(userId, directoryId);
    }

    public List<Directory> getHierarchy(UUID userId, UUID directoryId) throws DirectoryNotFoundException {
        var nextDirectoryId = directoryId;
        var path = new LinkedList<Directory>();
        while (nextDirectoryId != null) {
            var directory = get(userId, nextDirectoryId);
            path.addFirst(directory);
            nextDirectoryId = directory.parentId;
            if (directoryId.equals(nextDirectoryId)) {
                throw new IllegalStateException("Detected invalid data causing infinite loop");
            }
        }
        return path;
    }

    public void remove(
        UUID userId,
        UUID directoryId
    ) throws DirectoryNotFoundException, DirectoryValidationException, DirectoryNotUpdatedException {
        var directory = get(userId, directoryId);
        remove(directory);
    }

    public void remove(Directory directory) throws DirectoryValidationException, DirectoryNotUpdatedException {
        if (!directory.isRemoved()) {
            directory.updateStatus(DirectoryStatus.REMOVED);
            update(directory);
        }
    }

    public void update(
        UUID userId,
        UUID directoryId,
        DirectoryUpdate update
    ) throws DirectoryNotFoundException, DirectoryValidationException, DirectoryNotUpdatedException {
        var directory = get(userId, directoryId);
        if (update.parentId() != null) {
            directory.updateParentId(update.parentId());
        }
        if (update.unsetParentId()) {
            directory.updateParentId(null);
        }
        if (update.name() != null) {
            directory.updateName(update.name());
        }
        update(directory);
    }

    public void update(Directory directory) throws DirectoryValidationException, DirectoryNotUpdatedException {
        directory.updateModificationTime(new Time());
        directory.updateVersion(directory.version + 1);
        var result = Transaction.run(() -> DirectoryPersistence.update(directory));
        if (!result) {
            throw new DirectoryNotUpdatedException();
        }
    }

    public void delete(Directory directory) throws IllegalDirectoryStatusException, DirectoryNotFoundException {
        if (!directory.isRemoved()) {
            throw new IllegalDirectoryStatusException(directory.status);
        }
        var result = Transaction.run(() -> DirectoryPersistence.delete(directory));
        if (!result) {
            throw new DirectoryNotFoundException();
        }
    }
}
