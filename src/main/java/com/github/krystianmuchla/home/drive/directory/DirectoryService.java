package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DirectoryService {
    public static UUID create(UUID userId, UUID parentId, String path) {
        var id = UUID.randomUUID();
        var directory = new Directory(id, userId, parentId, path);
        DirectoryPersistence.create(directory);
        return directory.id();
    }

    public static Directory get(UUID userId, UUID directoryId) {
        var directory = DirectoryPersistence.readById(userId, directoryId);
        if (directory == null) {
            throw new NotFoundException();
        }
        return directory;
    }

    public static List<Directory> getPath(UUID userId, UUID directoryId) {
        var path = new LinkedList<Directory>();
        while (directoryId != null) {
            var directory = get(userId, directoryId);
            path.addFirst(directory);
            directoryId = directory.parentId();
        }
        return path;
    }
}
