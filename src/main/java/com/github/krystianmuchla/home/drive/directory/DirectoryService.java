package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DirectoryService {
    public static UUID create(UUID userId, UUID parentId, String name) {
        var id = UUID.randomUUID();
        var status = DirectoryStatus.CREATED;
        var creationTime = InstantFactory.create();
        var directory = new Directory(id, userId, status, parentId, name, creationTime);
        DirectoryPersistence.create(directory);
        return directory.id;
    }

    public static Directory get(UUID userId, UUID directoryId) {
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED, false);
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
        var directory = DirectoryPersistence.readByIdAndStatus(userId, directoryId, DirectoryStatus.CREATED, true);
        if (directory == null) {
            throw new NotFoundException();
        }
        remove(directory);
    }

    public static void remove(Directory directory) {
        if (!directory.isRemoved()) {
            directory.remove();
            update(directory);
        }
    }

    public static void update(Directory directory) {
        directory.modificationTime = InstantFactory.create();
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
