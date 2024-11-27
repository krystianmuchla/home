package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.NotFoundException;
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

    public static Directory get(UUID userId, UUID directoryId) {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new NotFoundException();
        }
        return directory;
    }

    public static List<Directory> getHierarchy(UUID userId, UUID directoryId) {
        var path = new LinkedList<Directory>();
        while (directoryId != null) {
            var directory = get(userId, directoryId);
            path.addFirst(directory);
            directoryId = directory.parentId;
        }
        return path;
    }

    public static void remove(UUID userId, UUID directoryId) {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED);
        if (directory == null) {
            throw new NotFoundException();
        }
        remove(directory);
    }

    public static void remove(Directory directory) {
        if (!directory.isRemoved()) {
            directory.updateStatus(DirectoryStatus.REMOVED);
            update(directory);
        }
    }

    public static void update(Directory directory) {
        var result = DirectoryPersistence.update(directory);
        if (!result) {
            throw new NotFoundException();
        }
    }

    public static void delete(Directory directory) {
        if (!directory.isRemoved()) {
            throw new InternalException(
                "Attempting to delete directory %s in wrong status %s".formatted(directory.id, directory.status)
            );
        }
        var result = DirectoryPersistence.delete(directory);
        if (!result) {
            throw new NotFoundException();
        }
    }
}
