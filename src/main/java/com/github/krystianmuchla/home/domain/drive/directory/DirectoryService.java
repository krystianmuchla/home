package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.domain.drive.directory.exception.DirectoryNotFoundException;
import com.github.krystianmuchla.home.domain.drive.directory.exception.DirectoryNotUpdatedException;
import com.github.krystianmuchla.home.domain.drive.directory.exception.IllegalDirectoryStatusException;
import com.github.krystianmuchla.home.infrastructure.persistence.drive.DirectoryPersistence;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DirectoryService {
    public static UUID create(UUID userId, UUID parentId, String name) {
        var directory = new Directory(userId, parentId, name);
        DirectoryPersistence.create(directory);
        return directory.id;
    }

    public static Directory get(UUID userId, UUID directoryId) throws DirectoryNotFoundException {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new DirectoryNotFoundException();
        }
        return directory;
    }

    public static List<Directory> getHierarchy(UUID userId, UUID directoryId) throws DirectoryNotFoundException {
        var path = new LinkedList<Directory>();
        while (directoryId != null) {
            var directory = get(userId, directoryId);
            path.addFirst(directory);
            directoryId = directory.parentId;
        }
        return path;
    }

    public static void remove(
        UUID userId,
        UUID directoryId
    ) throws DirectoryNotFoundException, DirectoryNotUpdatedException {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new DirectoryNotFoundException();
        }
        remove(directory);
    }

    public static void remove(Directory directory) throws DirectoryNotUpdatedException {
        if (!directory.isRemoved()) {
            directory.updateStatus(DirectoryStatus.REMOVED);
            update(directory);
        }
    }

    public static void update(Directory directory) throws DirectoryNotUpdatedException {
        var result = DirectoryPersistence.update(directory);
        if (!result) {
            throw new DirectoryNotUpdatedException();
        }
    }

    public static void delete(Directory directory) throws IllegalDirectoryStatusException, DirectoryNotFoundException {
        if (!directory.isRemoved()) {
            throw new IllegalDirectoryStatusException(directory.status);
        }
        var result = DirectoryPersistence.delete(directory);
        if (!result) {
            throw new DirectoryNotFoundException();
        }
    }
}
