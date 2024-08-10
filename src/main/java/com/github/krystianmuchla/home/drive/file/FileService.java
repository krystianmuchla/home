package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

import java.util.UUID;

public class FileService {
    public static UUID create(UUID userId, UUID directoryId, String path) {
        var id = UUID.randomUUID();
        var file = new File(id, userId, directoryId, path);
        FilePersistence.create(file);
        return file.id();
    }

    public static File get(UUID userId, UUID fileId) {
        var file = FilePersistence.readById(userId, fileId);
        if (file == null) {
            throw new NotFoundException();
        }
        return file;
    }
}
