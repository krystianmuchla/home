package com.github.krystianmuchla.home.domain.drive.directory;

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
        var path = new LinkedList<Directory>();
        while (directoryId != null) {
            var directory = get(userId, directoryId);
            path.addFirst(directory);
            directoryId = directory.parentId;
        }
        return path;
    }

    public void remove(
        UUID userId,
        UUID directoryId
    ) throws DirectoryNotFoundException, DirectoryNotUpdatedException {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new DirectoryNotFoundException();
        }
        remove(directory);
    }

    public void remove(Directory directory) throws DirectoryNotUpdatedException {
        if (!directory.isRemoved()) {
            directory.updateStatus(DirectoryStatus.REMOVED);
            update(directory);
        }
    }

    public void update(Directory directory) throws DirectoryNotUpdatedException {
        directory.updateModificationTime();
        directory.updateVersion();
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
