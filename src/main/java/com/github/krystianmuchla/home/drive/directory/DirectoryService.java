package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DirectoryService {
    public static UUID create(final UUID userId, final UUID parentId, final String path) {
        final var id = UUID.randomUUID();
        final var directory = new Directory(id, userId, parentId, path);
        DirectoryPersistence.create(directory);
        return directory.id();
    }

    public static Directory get(final UUID userId, final UUID directoryId) {
        final var directory = DirectoryPersistence.readById(userId, directoryId);
        if (directory == null) {
            throw new NotFoundException();
        }
        return directory;
    }

    public static List<Directory> getPath(final UUID userId, UUID directoryId) {
        final var path = new LinkedList<Directory>();
        while (directoryId != null) {
            final var directory = get(userId, directoryId);
            path.addFirst(directory);
            directoryId = directory.parentId();
        }
        return path;
    }
}
