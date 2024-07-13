package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.exception.http.NotFoundException;

import java.util.UUID;

public class FileService {
    public static UUID create(final UUID userId, final UUID directoryId, final String path) {
        final var id = UUID.randomUUID();
        final var file = new File(id, userId, directoryId, path);
        FilePersistence.create(file);
        return file.id();
    }

    public static File get(final UUID userId, final UUID fileId) {
        final var file = FilePersistence.readById(userId, fileId);
        if (file == null) {
            throw new NotFoundException();
        }
        return file;
    }
}
