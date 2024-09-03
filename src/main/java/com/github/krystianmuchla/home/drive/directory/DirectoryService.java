package com.github.krystianmuchla.home.drive.directory;

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
        var directory = DirectoryPersistence.read(userId, directoryId);
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
}
